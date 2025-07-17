package tassproject.authservice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.authservice.Citizen;
import tassproject.authservice.CitizenRepository;
import tassproject.authservice.RoleRepository;
import tassproject.authservice.repository.UserProfileRepository;
import tassproject.authservice.UserRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository         users;
    private final RoleRepository         roles;
    private final CitizenRepository      citizens;
    private final UserProfileRepository  profiles;
    private final PasswordEncoder        passwordEncoder;
    private final JwtUtil                jwtUtil;

    /* ------------------ REGISTER ------------------ */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String username = req.email().toLowerCase();
        if (users.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username già in uso");
        }
        if (citizens.existsByCf(req.cf())) {
            throw new IllegalArgumentException("CF già presente");
        }

        // 1) Salva cittadino
        UUID cid = UUID.randomUUID();
        String tipoUtente = switch (req.role()) {
            case PATIENT    -> "PAZIENTE";
            case DOCTOR     -> "MEDICO";
            case PHARMACIST -> "FARMACISTA";
            case ADMIN      -> "ADMIN";
        };
        Citizen citizen = new Citizen(
                cid,
                req.cf(),
                req.nome(),
                req.cognome(),
                req.dataNascita(),
                req.luogoNascita(),
                req.cittaResidenza(),
                tipoUtente
        );
        citizens.save(citizen);

        // 2) Salva user & ruolo
        RoleEntity roleEnt = roles.findByName(req.role().name())
                .orElseThrow(() -> new IllegalStateException("Ruolo non trovato"));
        User user = new User(
                username,
                passwordEncoder.encode(req.password()),
                Set.of(roleEnt)
        );
        users.save(user);

        // 3) Mappa user ↔ citizen
        profiles.save(new UserProfile(user.getId(), cid));

        // 4) JWT completo di citizen_id
        return AuthResponse.bearer(jwtUtil.generateToken(user, cid));
    }

    /* ------------------- LOGIN -------------------- */
    public AuthResponse login(LoginRequest req) {
        User user = users.findByUsername(req.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenziali non valide"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenziali non valide");
        }

        // Recupera il citizenId dal mapping (usando findById)
        UUID citizenId = profiles.findById(user.getId())
                .map(UserProfile::getCitizenId)
                .orElseThrow(() -> new IllegalStateException("Profilo utente mancante"));

        return AuthResponse.bearer(jwtUtil.generateToken(user, citizenId));
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
