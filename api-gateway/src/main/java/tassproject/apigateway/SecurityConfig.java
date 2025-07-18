package tassproject.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /* ───────────────────────────────────────────────────────────────
     *  JWT → GrantedAuthorities converter
     * ─────────────────────────────────────────────────────────────── */
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter grantConverter() {
        JwtGrantedAuthoritiesConverter conv = new JwtGrantedAuthoritiesConverter();
        conv.setAuthorityPrefix("ROLE_");
        conv.setAuthoritiesClaimName("role");
        JwtAuthenticationConverter jwtConv = new JwtAuthenticationConverter();
        jwtConv.setJwtGrantedAuthoritiesConverter(conv);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConv);
    }

    /* ───────────────────────────────────────────────────────────────
     *  JWT Decoder (HMAC‑SHA256) – obbliga la variabile JWT_SECRET
     * ─────────────────────────────────────────────────────────────── */
    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            @Value("${jwt.secret:}") String secretValue            // ← solo env/prop
    ) {
        if (secretValue == null || secretValue.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET non impostato: definiscilo nei parametri di " +
                            "ambiente o in application.properties/yml");
        }

        /* prova a decodificare Base64, altrimenti usa raw bytes UTF‑8 */
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secretValue);
        } catch (IllegalArgumentException ex) {
            keyBytes = secretValue.getBytes(StandardCharsets.UTF_8);
        }
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    /* ───────────────────────────────────────────────────────────────
     *  Spring Security (WebFlux) filter‑chain
     * ─────────────────────────────────────────────────────────────── */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtAuthenticationConverterAdapter grantConverter
    ) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/oauth2/**", "/login/**",
                                "/api/v1/auth/**", "/actuator/**")
                        .permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantConverter))
                )
                .cors(Customizer.withDefaults());

        return http.build();
    }
}
