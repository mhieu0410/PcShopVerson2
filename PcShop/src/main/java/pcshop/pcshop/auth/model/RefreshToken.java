package pcshop.pcshop.auth.dto.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="refresh_tokens",
    indexes = {
        @Index(name="idx_rt_user", columnList="user_id"),
        @Index(name="idx_rt_jti", columnList="jti"),
        @Index(name="idx_rt_expires", columnList="expires_at"),
        @Index(name="idx_rt_revoked", columnList="revoked_at")
    })
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(nullable = false, length = 64)
    private String jti;

    @Column(name="device_info", length = 255)
    private String deviceInfo;

    @Column(length = 64)
    private String ip;

    @Column(name="user_agent", length = 255)
    private String userAgent;

    @Column(name="created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name="revoked_at")
    private LocalDateTime revokedAt;

    @Column(name="replaced_by", length = 64)
    private String replacedBy;
}
