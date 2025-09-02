package pcshop.pcshop.config;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
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

    public @NotBlank String getIssuer() {
        return issuer;
    }

    public void setIssuer(@NotBlank String issuer) {
        this.issuer = issuer;
    }

    public @NotBlank String getSecret() {
        return secret;
    }

    public void setSecret(@NotBlank String secret) {
        this.secret = secret;
    }

    public @NotBlank Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(@NotBlank Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }

    public @NotBlank Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public void setRefreshTokenTtl(@NotBlank Duration refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public @NotBlank Duration getClockSkew() {
        return clockSkew;
    }

    public void setClockSkew(@NotBlank Duration clockSkew) {
        this.clockSkew = clockSkew;
    }
}
