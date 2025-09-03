package pcshop.pcshop.auth.dto.service.impl;

import org.springframework.stereotype.Service;
import pcshop.pcshop.auth.dto.model.EmailVerificationToken;
import pcshop.pcshop.auth.dto.repository.EmailVerificationTokenRepository;
import pcshop.pcshop.auth.dto.service.TokenService;
import pcshop.pcshop.auth.dto.util.TokenHash;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final EmailVerificationTokenRepository emailTokens;

    public TokenServiceImpl(EmailVerificationTokenRepository emailTokens) {
        this.emailTokens = emailTokens;
    }


    @Override
    public String issueEmailVerificationToken(Long userId, int ttlMinutes) {
        String raw = UUID.randomUUID().toString();
        String hash = TokenHash.sha256(raw);

        EmailVerificationToken t = EmailVerificationToken.builder()
                .userId(userId)
                .tokenHash(hash)
                .expiresAt(LocalDateTime.now().plusMinutes(ttlMinutes))
                .build();
        emailTokens.save(t);
        return raw; // trả về raw để gửi qua email
    }

    @Override
    public Long consumeEmailVerificationToken(String rawToken) {
        String hash = TokenHash.sha256(rawToken);
        var now = LocalDateTime.now();
        var opt = emailTokens.findByTokenHashAndExpiresAtAfterAndUsedAtIsNull(hash, now);
        if (opt.isEmpty()) return null;
        var token = opt.get();
        token.setUsedAt(now);
        emailTokens.save(token);
        return token.getUserId();
    }
}
