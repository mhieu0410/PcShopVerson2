package pcshop.pcshop.service;

import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pcshop.pcshop.dto.LoginRequest;
import pcshop.pcshop.dto.RegisterRequest;
import pcshop.pcshop.entity.RefreshToken;
import pcshop.pcshop.entity.Role;
import pcshop.pcshop.entity.User;
import pcshop.pcshop.entity.UserStatus;
import pcshop.pcshop.jwt.JwtService;
import pcshop.pcshop.repository.RefreshTokenRepository;
import pcshop.pcshop.repository.RoleRepository;
import pcshop.pcshop.repository.UserRepository;
import pcshop.pcshop.util.TokenHash;
import pcshop.pcshop.web.CookieUtil;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final EmailService email;
    private final JwtService jwt;
    private final RefreshTokenRepository refreshTokens;

    @Override
    public void register(RegisterRequest req, String appBaseUrl) {
        if (!req.agreeTos()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn cần đồng ý điều khoản (TOS).");
        }
        if (users.existsByEmail(req.email())) {
            // 409: đã tồn tại
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng.");
        }

        Role userRole = roles.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE USER chưa seed"));

        User u = User.builder()
                .email(req.email().trim().toLowerCase())
                .passwordHash(encoder.encode(req.password()))
                .displayName(req.displayName())
                .status(UserStatus.PENDING)
                .failedLogins(0)
                .roles(Set.of(userRole))
                .build();
        users.save(u);

        String rawToken = tokens.issueEmailVerificationToken(u.getId(), 30); // 30 phút
        String link = appBaseUrl + "/verify-email?token=" + rawToken;

        String body = """
                <h3> Verify your email </h3>
                <p>Nhấn vào link để kích hoạt tài khoản (hết hạn sau 30 phút):</p>
                <p><a href="%s">%s</a></p>
                <p>Nếu không phải bạn, hãy bỏ qua email này.</p>
                """.formatted(link, link);
        email.send(u.getEmail(), "[YourApp] Verify your email", body);
    }

    @Override
    public void verifyEmail(String rawToken) {
        Long userId = tokens.consumeEmailVerificationToken(rawToken);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token không hợp lệ hoặc đã hết hạn.");
        }
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại."));

        user.setStatus(UserStatus.ACTIVE);
        users.save(user);
    }

    @Override
    public String login(LoginRequest req, String ip, String userAgent, HttpServletResponse response) {
        User u = users.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng"));

        if (u.getStatus() == UserStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản chưa xác minh email.");
        if (u.getStatus() == UserStatus.BLOCKED)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đang bị khóa.");
        if (u.getStatus() == UserStatus.DISABLED)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đã bị vô hiệu.");

        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            int fails = (u.getFailedLogins() == null ? 0 : u.getFailedLogins()) + 1;
            u.setFailedLogins(fails);
            // (Đơn giản) nếu >=5 thì LOCKED (tạm không có unlock tự động)
            if (fails >= 5) u.setStatus(UserStatus.BLOCKED);
            users.save(u);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng");
        }

        //ok
        u.setFailedLogins(0);
        u.setLastLoginAt(LocalDateTime.now());
        users.save(u);

        //Access
        String access = jwt.generateAccess(u);

        // Refresh + rotation seed
        String jti = UUID.randomUUID().toString();
        var rolesStr = u.getRoles().stream().map(Role::getName).toList();
        String refreshRaw = jwt.generateRefresh(u.getId(), u.getEmail(), rolesStr, jti);

        // Lưu hash refresh
        RefreshToken rt = RefreshToken.builder()
                .userId(u.getId())
                .tokenHash(TokenHash.sha256(refreshRaw))
                .jti(jti)
                .deviceInfo(req.deviceInfo())
                .ip(ip)
                .userAgent(userAgent)
                .createdAt(null)
                .expiresAt(LocalDateTime.now().plusSeconds(jwt.getRefreshTtlSeconds()))
                .build();
        refreshTokens.save(rt);

        // Set cookie
        CookieUtil.serRefresh(response, refreshRaw, (int) jwt.getRefreshTtlSeconds());
        return access;
    }

    @Override
    public String refresh(String rawRefresh, String ip, String userAgent, HttpServletResponse response) {
        if (rawRefresh == null || rawRefresh.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");

        // parse & verify
        var jws = jwt.parse(rawRefresh);
        var claims = jws.getPayload();
        Long userId = Long.valueOf(claims.getSubject());
        String oldJti = claims.getId();

        // check DB (not revoked)
        var hash = TokenHash.sha256(rawRefresh);
        var dbRt = refreshTokens.findByTokenHashAndRevokedAtIsNull(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalid"));

        // rotate: revoke old, issue new
        String newJti = UUID.randomUUID().toString();
        String email = claims.get("email", String.class);
        @SuppressWarnings("unchecked")
        var roles = (java.util.List<String>) claims.get("roles");

        String newRefresh = jwt.generateRefresh(userId, email, roles, newJti);

        // revoke old
        dbRt.setRevokedAt(LocalDateTime.now());
        dbRt.setJti(newJti);
        refreshTokens.save(dbRt);

        // save new
        RefreshToken newRt = RefreshToken.builder()
                .userId(userId)
                .tokenHash(TokenHash.sha256(newRefresh))
                .jti(newJti)
                .ip(ip)
                .userAgent(userAgent)
                .expiresAt(LocalDateTime.now().plusSeconds(jwt.getRefreshTtlSeconds()))
                .build();
        refreshTokens.save(newRt);

        // set new cookie
        CookieUtil.serRefresh(response, newRefresh, (int) jwt.getRefreshTtlSeconds());

        // new access
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        String access = jwt.generateAccess(user);
        return access;
    }

    @Override
    public void logout(String rawRefresh, HttpServletResponse response) {
        if (rawRefresh != null && !rawRefresh.isBlank()) {
            var hash = TokenHash.sha256(rawRefresh);
            refreshTokens.findByTokenHashAndRevokedAtIsNull(hash).ifPresent(rt -> {
                rt.setRevokedAt(LocalDateTime.now());
                refreshTokens.save(rt);
            });
        }
        CookieUtil.clearRefresh(response);
    }

}
