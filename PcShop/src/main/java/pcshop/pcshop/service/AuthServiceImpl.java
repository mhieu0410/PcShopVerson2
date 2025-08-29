package pcshop.pcshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pcshop.pcshop.dto.RegisterRequest;
import pcshop.pcshop.entity.Role;
import pcshop.pcshop.entity.User;
import pcshop.pcshop.entity.UserStatus;
import pcshop.pcshop.repository.RoleRepository;
import pcshop.pcshop.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final EmailService email;

    @Override
    public void register(RegisterRequest req, String appBaseUrl) {
        if(!req.agreeTos()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn cần đồng ý điều khoản (TOS).");
        }
        if(users.existsByEmail(req.email())){
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
        if(userId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token không hợp lệ hoặc đã hết hạn.");
        }
        var user = users.findById(userId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại."));

        user.setStatus(UserStatus.ACTIVE);
        users.save(user);
    }
}
