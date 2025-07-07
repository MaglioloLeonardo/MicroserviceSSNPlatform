package tassproject.authservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tassproject.authservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /* ───────── REGISTER ───────── */

    public AuthResponse register(RegisterRequest request) {
        String username = request.email().toLowerCase();

        if (users.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username già in uso");
        }

        User user = new User(
                username,
                passwordEncoder.encode(request.password()),
                request.role()
        );

        users.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse("Bearer", token);
    }

    /* ─────────  LOGIN  ───────── */

    public AuthResponse login(LoginRequest request) {
        User user = users.findByUsername(request.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenziali non valide"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenziali non valide");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse("Bearer", token);
    }
}
