package kz.enu.uniAttend.config;


import kz.enu.uniAttend.util.encoder.BCryptPasswordEncoder;
import kz.enu.uniAttend.util.token.RandomStringTokenGenerator;
import kz.enu.uniAttend.util.token.TokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TokenGenerator apiKeyGenerator() {
        return new RandomStringTokenGenerator(128);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
