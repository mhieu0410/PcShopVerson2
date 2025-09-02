package pcshop.pcshop.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import pcshop.pcshop.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtService {
    private final Key key;
    private final String issuer;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final long clockSkewSeconds;

    public JwtService(pcshop.pcshop.config.JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        this.issuer = props.getIssuer();
        this.accessTtlSeconds = props.getAccessTokenTtl().toSeconds();
        this.refreshTtlSeconds = props.getRefreshTokenTtl().toSeconds();
        this.clockSkewSeconds = props.getClockSkew().toSeconds();
    }

    public String generateAccess(User u) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(u.getId()))
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .id(UUID.randomUUID().toString()) // jti access
                .claim("email", u.getEmail())
                .claim("roles", u.getRoles().stream().map(r -> r.getName()).toList())
                .signWith(key)
                .compact();
    }

    public String generateRefresh(Long userId, String email, List<String> roles, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .id(jti) // jti refresh
                .claim("email", email)
                .claim("roles", roles)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .clockSkewSeconds(clockSkewSeconds)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }

    public long getAccessTtlSeconds() { return accessTtlSeconds; }
    public long getRefreshTtlSeconds() { return refreshTtlSeconds; }
}
