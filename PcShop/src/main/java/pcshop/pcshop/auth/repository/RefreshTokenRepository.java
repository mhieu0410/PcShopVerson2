package pcshop.pcshop.auth.dto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.pcshop.auth.dto.model.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);
    Optional<RefreshToken> findByJtiAndRevokedAtIsNull(String jti);
    List<RefreshToken> findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(Long userId, LocalDateTime now);
    int deleteByUserId(Long userId);
}
