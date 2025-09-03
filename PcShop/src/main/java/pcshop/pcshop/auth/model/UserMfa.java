package pcshop.pcshop.auth.dto.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_mfa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserMfa {
    @Id
    @Column(name="user_id")
    private Long userId;

    @Column(name="secret_encrypted", length = 255)
    private String secretEncryted;

    @Column(name="enabled_at")
    private LocalDateTime enabledAt;
}
