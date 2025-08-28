package pcshop.pcshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class CorsConfig {

    private final CorsProps props;

    public CorsConfig(CorsProps props){
        this.props = props;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(props.getAllowedOrigins());
        cfg.setAllowedMethods(List.of(props.getAllowedMethods().split(",")));
        cfg.setAllowedHeaders(List.of(props.getAllowedHeaders().split(",")));
        cfg.setAllowCredentials(props.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}

