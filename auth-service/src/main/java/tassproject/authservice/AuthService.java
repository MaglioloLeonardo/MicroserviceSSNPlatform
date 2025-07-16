package tassproject.authservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.authservice.repository.RoleRepository;
import tassproject.authservice.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /* ------------------ REGISTER ------------------ */
    public AuthResponse register(RegisterRequest request) {
        String username = request.email().toLowerCase();
        if (users.findByUsername(username).isPresent())
            throw new IllegalArgumentException("Username già in uso");

        RoleEntity roleEntity = roles.findByName(request.role().name())
                .orElseThrow(() -> new IllegalStateException("Ruolo non trovato"));

        User user = new User(
                username,
                passwordEncoder.encode(request.password()),
                Set.of(roleEntity)
        );
        users.save(user);

        String token = jwtUtil.generateToken(user);
        return AuthResponse.bearer(token);
    }

    /* ------------------- LOGIN -------------------- */
    public AuthResponse login(LoginRequest request) {
        User user = users.findByUsername(request.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenziali non valide"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new IllegalArgumentException("Credenziali non valide");

        return AuthResponse.bearer(jwtUtil.generateToken(user));
    }

    /* ------------------- LOGOUT ------------------- */
    @Transactional
    public void logout(String username) {
        users.findByUsername(username.toLowerCase()).ifPresent(u -> {
            u.setSessionVersion(u.getSessionVersion() + 1);
            users.save(u);
        });
    }

    /* --------------------- ME --------------------- */
    public User me(String username) {
        return users.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new IllegalStateException("Utente non trovato"));
    }
}
