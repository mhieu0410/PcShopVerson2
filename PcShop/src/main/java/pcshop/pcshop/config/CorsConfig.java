package pcshop.pcshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Value("#{'${app.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(allowedOrigins);
        cfg.setAllowedMethods(List.of(allowedMethods.split(",")));
        cfg.setAllowedHeaders(List.of(allowedHeaders.split(",")));
        cfg.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}

