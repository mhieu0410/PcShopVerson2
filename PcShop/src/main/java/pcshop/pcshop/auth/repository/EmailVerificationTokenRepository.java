package pcshop.pcshop.auth.dto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.pcshop.auth.dto.model.EmailVerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByTokenHashAndExpiresAtAfterAndUsedAtIsNull(String token, LocalDateTime now);
}
