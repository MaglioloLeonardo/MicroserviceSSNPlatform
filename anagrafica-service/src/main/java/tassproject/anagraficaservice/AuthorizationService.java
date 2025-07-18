package tassproject.anagraficaservice;

import jakarta.annotation.Nonnull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tassproject.anagraficaservice.PrescriptionClient;

import java.util.List;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class AuthorizationService {

    private final PrescriptionClient prescriptions;

    public AuthorizationService(PrescriptionClient prescriptions) {
        this.prescriptions = prescriptions;
    }

    /* —— Utility JWT —— */
    private Authentication auth() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
    }

    private String role() {
        return auth().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findFirst().orElse("");
    }

    public UUID userId() {   // esposto anche al Controller
        var token = (JwtAuthenticationToken) auth();
        String id = token.getToken().getClaimAsString("citizen_id");
        if (id == null) throw new AccessDeniedException("Missing claim citizen_id");
        return UUID.fromString(id);
    }

    /* —— Verifica diritti su un cittadino —— */
    public void assertCanAccessCitizen(@Nonnull UUID citizenId) {
        switch (role()) {
            case "ROLE_PATIENT" -> {
                if (!userId().equals(citizenId))
                    deny("Puoi accedere solo alla tua anagrafica.");
            }
            case "ROLE_DOCTOR" -> {
                if (userId().equals(citizenId)) return;  // medico che guarda se stesso
                List<UUID> myPatients = prescriptions.patientsOfDoctor(userId());
                if (!myPatients.contains(citizenId))
                    deny("Il paziente non è in carico a questo medico.");
            }
            case "ROLE_ADMIN" -> { /* sempre ok */ }
            default -> deny("Accesso non consentito.");
        }
    }

    /* —— Verifica diritti su lista pazienti —— */
    public void assertCanAccessDoctorPatients(@Nonnull UUID doctorId) {
        switch (role()) {
            case "ROLE_DOCTOR" -> {
                if (!userId().equals(doctorId))
                    deny("Puoi elencare solo i tuoi pazienti.");
            }
            case "ROLE_ADMIN" -> { }
            default -> deny("Accesso non consentito.");
        }
    }

    private void deny(String msg) {
        throw new AccessDeniedException(msg);
    }
}
