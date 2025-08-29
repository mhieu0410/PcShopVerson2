package pcshop.pcshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="email_verification_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="token_hash", nullable=false, length=255)
    private String tokenHash;

    @Column(name="expires_at", nullable=false)
    private LocalDateTime expiresAt;

    @Column(name="used_at")
    private LocalDateTime usedAt;
}
