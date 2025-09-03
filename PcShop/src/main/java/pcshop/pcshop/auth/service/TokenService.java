package pcshop.pcshop.auth.dto.service;

import org.springframework.stereotype.Repository;

public interface TokenService {
    String issueEmailVerificationToken(Long userId, int ttlMinutes); // trả về raw token
    Long consumeEmailVerificationToken(String rawToken); //  trả userId nếu ok, null nếu fail
}
