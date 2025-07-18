package tassproject.authservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        cfg.setAllowedMethods(List.of("*"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);

        return new CorsFilter(source);
    }
}
