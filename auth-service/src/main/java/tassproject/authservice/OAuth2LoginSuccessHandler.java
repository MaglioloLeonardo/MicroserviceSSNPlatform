package tassproject.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tassproject.authservice.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(),
                    Map.of("error","Unauthorized","message","No email provided by Google"));
            return;
        }

        // trova o crea un User locale
        User user = userRepository.findByUsername(email.toLowerCase())
                .orElseGet(() -> {
                    User u = new User(
                            email.toLowerCase(),
                            "",                  // password vuota (non useremo il form-login)
                            Role.PATIENT         // default role
                    );
                    return userRepository.save(u);
                });

        // genera il JWT
        String jwt = jwtUtil.generateToken(user);

        // 🚩 Imposta il JWT in un cookie HttpOnly (opzionale, ma consigliato)
        Cookie jwtCookie = new Cookie("access_token", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(request.isSecure());
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60); // 1 ora, oppure configurable

        response.addCookie(jwtCookie);

        // rispondi JSON (se vuoi che il frontend legga il token e lo gestisca)
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(),
                Map.of(
                        "tokenType","Bearer",
                        "accessToken", jwt
                )
        );
    }
}
