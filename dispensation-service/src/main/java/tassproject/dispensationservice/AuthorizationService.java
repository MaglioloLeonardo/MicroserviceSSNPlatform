package tassproject.dispensationservice.security;

import jakarta.annotation.Nonnull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tassproject.dispensationservice.client.PrescriptionClient;
import tassproject.dispensationservice.repository.DispensationRepository;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class AuthorizationService {

    private final DispensationRepository dispensations;
    private final PrescriptionClient prescriptions;   // ← nuovo client REST

    public AuthorizationService(DispensationRepository disp,
                                PrescriptionClient presc) {
        this.dispensations = disp;
        this.prescriptions = presc;
    }

    /* -------- utilities JWT -------- */
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

    /* ---------- checks ------------ */
    public void assertCanAccessPharmacist(@Nonnull UUID pharmacistId) {
        switch (role()) {
            case "ROLE_PHARMACIST" -> {
                if (!userId().equals(pharmacistId))
                    deny("Puoi vedere solo i tuoi ordini.");
            }
            case "ROLE_ADMIN" -> { }
            default -> deny("Solo il farmacista interessato può accedere.");
        }
    }

    public void assertCanAccessPatient(@Nonnull UUID patientId) {
        switch (role()) {
            case "ROLE_PATIENT" -> {
                if (!userId().equals(patientId))
                    deny("Puoi vedere solo i tuoi dati.");
            }
            case "ROLE_PHARMACIST" -> {
                /* verifichiamo di aver dispensato al paziente */
                boolean served = dispensations.findByDispensedBy(userId()).stream()
                        .map(d -> prescriptions.getPrescription(d.getPrescriptionId()))
                        .filter(dto -> dto != null && dto.patientId().equals(patientId))
                        .findAny().isPresent();
                if (!served) deny("Non hai mai servito questo paziente.");
            }
            case "ROLE_ADMIN" -> { }
            default -> deny("Accesso non consentito.");
        }
    }

    private void deny(String msg) {
        throw new AccessDeniedException(msg);
    }
}
