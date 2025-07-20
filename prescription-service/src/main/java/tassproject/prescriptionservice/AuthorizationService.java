package tassproject.prescriptionservice;

import jakarta.annotation.Nonnull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tassproject.prescriptionservice.repository.PrescriptionRepository;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class AuthorizationService {

    private final PrescriptionRepository prescriptions;

    public AuthorizationService(PrescriptionRepository prescriptions) {
        this.prescriptions = prescriptions;
    }

    /* ------------------------------------------------------------------
       Utilities
       ------------------------------------------------------------------ */
    private Authentication auth() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
    }

    private String role() {
        return auth().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findFirst().orElse("");
    }

    private UUID userId() {
        var token = (JwtAuthenticationToken) auth();
        String id = token.getToken().getClaimAsString("citizen_id");
        if (id == null) throw new AccessDeniedException("Missing claim citizen_id");
        return UUID.fromString(id);
    }

    /* ------------------------------------------------------------------
       Checks
       ------------------------------------------------------------------ */

    /**
     * Verifica che chi chiede o modifica dati del paziente ne abbia diritto.
     * <ul>
     *   <li>Il paziente può vedere solo sé stesso.</li>
     *   <li>Qualsiasi medico e l’amministratore possono operare su qualunque paziente.</li>
     *   <li>Il farmacista non può accedere ai dati clinici del paziente.</li>
     * </ul>
     */
    public void assertCanAccessPatient(@Nonnull UUID patientId) {
        switch (role()) {
            case "ROLE_PATIENT" -> {
                if (!userId().equals(patientId))
                    deny("Puoi accedere solo alle tue informazioni.");
            }
            case "ROLE_DOCTOR", "ROLE_ADMIN" -> { /* sempre permesso */ }
            case "ROLE_PHARMACIST" -> deny("Un farmacista non può leggere dati clinici del paziente.");
            default -> deny("Ruolo non riconosciuto.");
        }
    }

    /**
     * Verifica che chi chiede dati del medico sia il medico stesso
     * oppure un suo paziente.
     */
    public void assertCanAccessDoctor(@Nonnull UUID doctorId) {
        switch (role()) {
            case "ROLE_DOCTOR" -> {
                if (!userId().equals(doctorId)) deny("Non sei quel medico.");
            }
            case "ROLE_PATIENT" -> {
                if (!prescriptions.existsByDoctorIdAndPatientId(doctorId, userId()))
                    deny("Quel medico non ti ha mai in cura.");
            }
            case "ROLE_ADMIN" -> { }
            default -> deny("Accesso non consentito.");
        }
    }

    private void deny(String msg) {
        throw new AccessDeniedException(msg);
    }
}
