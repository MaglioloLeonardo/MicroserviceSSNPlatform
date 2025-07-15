package tassproject.authservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tassproject.authservice.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    /* ---------------- REGISTER ---------------- */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest body) {
        return auth.register(body);
    }

        /* ---------------- LOGIN ------------------- */
                @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest body,
            HttpServletResponse response
    ) {
                // 1) Effettuo l’autenticazione e genero il JWT
                        AuthResponse authResp = auth.login(body);

                        // 2) Imposto il cookie HttpOnly con il token
                                Cookie jwtCookie = new Cookie("access_token", authResp.accessToken());
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true);    // in produzione lascia true
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(3600);    // durata in secondi (come jwt.expiration)
                response.addCookie(jwtCookie);

                        // 3) Ritorno l’intero AuthResponse (tokenType + accessToken) come JSON
                                return authResp;
            }

    /* ---------------- LOGIN AUTOMATICO -------- */
    @GetMapping("/me")
    public User me(Authentication authentication) {
        if (authentication == null)
            throw new IllegalStateException("Non autenticato");
        return auth.me(authentication.getName());
    }

    /* ---------------- LOGOUT COMPLETO --------- */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(Authentication authentication,
                       HttpServletResponse response) {

        if (authentication != null) {
            auth.logout(authentication.getName());
        }
        /* cancella eventuale cookie JWT (se lo usi) */
        Cookie cookie = new Cookie("access_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
