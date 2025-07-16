package tassproject.authservice;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tassproject.authservice.repository.UserRepository;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final UserRepository users;
    private final RestAuthenticationEntryPoint restEntryPoint;
    private final RestAccessDeniedHandler      restAccessDeniedHandler;
    private final OAuth2LoginSuccessHandler    oAuth2LoginSuccessHandler;

    /* -------- UserDetailsService: ruoli dal join‑table -------- */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> users
                .findByUsername(username.toLowerCase())
                .map(u -> org.springframework.security.core.userdetails.User
                        .withUsername(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRoles().stream()
                                .map(RoleEntity::getName)
                                .toArray(String[]::new))
                        .build())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utente non trovato: " + username));
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    /* ---------- JWT (HMAC‑SHA256) ---------- */
    @Bean
    public JwtEncoder jwtEncoder(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SecretKey key   = Keys.hmacShaKeyFor(keyBytes);
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SecretKey key   = Keys.hmacShaKeyFor(keyBytes);

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).build();
        OAuth2TokenValidator<Jwt> defVal  = JwtValidators.createDefault();
        OAuth2TokenValidator<Jwt> verVal  = new SessionVersionValidator(users);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defVal, verVal));

        return decoder;
    }

    /* ---------- CORS dev SPA ---------- */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("Authorization","Content-Type")
                .allowCredentials(true);
    }

    /* ---------- Security filter‑chain ---------- */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                        .requestMatchers("/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2LoginSuccessHandler));

        return http.build();
    }
}
