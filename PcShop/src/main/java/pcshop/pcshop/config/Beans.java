package pcshop.pcshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@Configuration
public class Beans {
    @Bean
    PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(12);}
    @Bean
    Clock clock(){
        return Clock.systemUTC();
    }

}
