package kz.enu.uniAttend.config;

import kz.enu.uniAttend.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;



    private static final String[] COMMON_EXCLUDE_PATTERNS = {
            "/api/v1/user/password/reset-invite",
            "/api/v1/auth/sign-in",
            "/api/v1/version",
            "/api/v1/user/password/reset",
            "/swagger-ui.html",
            "/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/user/password/recovery"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .order(1)
                .excludePathPatterns(COMMON_EXCLUDE_PATTERNS)
                .excludePathPatterns("/api/v1/bitrix24/**");
    }




    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8081", "exp://192.168.1.*:8081", "https://uniattend.netlify.app/") // или "*" для разрешения всех
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .exposedHeaders("Auth-token", "auth-token")
                .allowCredentials(true);
    }

}

