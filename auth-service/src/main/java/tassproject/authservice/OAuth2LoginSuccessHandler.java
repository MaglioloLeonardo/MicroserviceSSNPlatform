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

        // 1) Estrai l’OAuth2User e la sua email
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 2) Se Google non ci fornisce l’email, rimandiamo 401 con JSON di errore
        if (email == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(),
                    Map.of(
                            "error", "Unauthorized",
                            "message", "No email provided by Google"
                    )
            );
            return;
        }

        // 3) Trova o crea l’utente locale
        User user = userRepository.findByUsername(email.toLowerCase())
                .orElseGet(() -> {
                    User u = new User(
                            email.toLowerCase(),
                            "",            // password vuota perché non useremo il form-login
                            Role.PATIENT   // ruolo di default
                    );
                    return userRepository.save(u);
                });

        // 4) Genera il JWT
        String jwt = jwtUtil.generateToken(user);

        // 5) Imposta il JWT in un cookie HttpOnly
        Cookie jwtCookie = new Cookie("access_token", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(request.isSecure());
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60); // 1 ora
        // Se servisse cross-site:
        // jwtCookie.setSecure(true);
        // jwtCookie.setSameSite("None");
        response.addCookie(jwtCookie);

        // 6) Redirect diretto al frontend per la selezione del ruolo
        response.sendRedirect("http://localhost:3000/seleziona-ruolo");
    }
}
