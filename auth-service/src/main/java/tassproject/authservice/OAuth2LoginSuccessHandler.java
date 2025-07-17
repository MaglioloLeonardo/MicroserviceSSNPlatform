package tassproject.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tassproject.authservice.RoleRepository;
import tassproject.authservice.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        /* -------- 1) estrai e‑mail -------- */
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(),
                    Map.of("error", "Unauthorized", "message", "No email provided by Google"));
            return;
        }

        /* -------- 2) trova ruolo PATIENT -------- */
        RoleEntity patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(() -> new IllegalStateException("Ruolo PATIENT mancante a DB"));

        /* -------- 3) upsert utente -------- */
        User user = userRepository.findByUsername(email.toLowerCase())
                .orElseGet(() -> userRepository.save(
                        new User(email.toLowerCase(), "", Set.of(patientRole))
                ));

        /* -------- 4) JWT + cookie -------- */
        String jwt = jwtUtil.generateToken(user);
        Cookie cookie = new Cookie("access_token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        /* -------- 5) redirect -------- */
        response.sendRedirect("http://localhost:3000/seleziona-ruolo");
    }
}
