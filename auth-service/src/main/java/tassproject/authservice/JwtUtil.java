package tassproject.authservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretValue;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretValue);
        } catch (IllegalArgumentException | DecodingException ex) {
            keyBytes = secretValue.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "Il JWT_SECRET configurato è troppo corto (" +
                            (keyBytes.length * 8) + " bit). Deve essere almeno 256 bit.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Nuovo overload che include il claim "citizen_id".
     */
    public String generateToken(User user, UUID citizenId) {
        var builder = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("ver", user.getSessionVersion())
                .claim("role", user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs));

        if (citizenId != null) {
            builder.claim("citizen_id", citizenId.toString());
        }
        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Metodo legacy (es. OAuth2 login) ‒ genera comunque un token ma **senza** citizen_id.
     */
    public String generateToken(User user) {
        return generateToken(user, null);
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


}
