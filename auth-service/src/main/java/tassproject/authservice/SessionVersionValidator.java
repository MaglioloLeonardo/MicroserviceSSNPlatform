package tassproject.authservice;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import tassproject.authservice.repository.UserRepository;

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
        Integer ver      = token.getClaim("ver");
        String  username = token.getSubject();

        if (ver == null || username == null) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Missing 'ver' claim or subject", null));
        }

        boolean valid = users.findByUsername(username.toLowerCase())
                .map(u -> ver.equals(u.getSessionVersion()))
                .orElse(false);

        return valid
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Session revoked", null));
    }
}
