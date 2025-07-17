package tassproject.authservice;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import tassproject.authservice.UserRepository;

/**
 * Rifiuta i JWT la cui 'ver' non coincide con la sessionVersion salvata a DB.
 */
public class SessionVersionValidator implements OAuth2TokenValidator<Jwt> {

    private final UserRepository users;

    public SessionVersionValidator(UserRepository users) {
        this.users = users;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        // Prendi il claim "ver" come Number (gestisce Long, Integer, ecc.)
        Number rawVer = token.getClaim("ver");
        String username = token.getSubject();

        if (rawVer == null || username == null) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Missing 'ver' claim or subject", null));
        }

        // Converte in int (o long se preferisci) e confronta con il DB
        int ver = rawVer.intValue();

        boolean valid = users.findByUsername(username.toLowerCase())
                .map(u -> ver == u.getSessionVersion())  // qui u.getSessionVersion() è un Integer
                .orElse(false);

        return valid
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Session revoked", null));
    }
}
