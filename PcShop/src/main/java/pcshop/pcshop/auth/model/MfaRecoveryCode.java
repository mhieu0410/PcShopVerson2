package pcshop.pcshop.auth.dto.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="mfa_recovery_codes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MfaRecoveryCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="code_hash", nullable = false, length = 255)
    private String codeHash;

    @Column(name="used_at")
    private LocalDateTime usedAt;
}
