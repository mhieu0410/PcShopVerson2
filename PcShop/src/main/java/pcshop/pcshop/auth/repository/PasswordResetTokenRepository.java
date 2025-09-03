package pcshop.pcshop.auth.dto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pcshop.pcshop.auth.dto.model.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndExpiresAtAfterAndUsedAtIsNull(String tokenHash, LocalDateTime now);
}
