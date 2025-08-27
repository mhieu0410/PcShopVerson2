package pcshop.pcshop.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    @NotBlank private String issuer;
    @NotBlank private String secret;
    @NotBlank private Duration accessTokenTtl;
    @NotBlank private Duration refreshTokenTtl;
    @NotBlank private Duration clockSkew;
}
