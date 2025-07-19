package tassproject.anagraficaservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class PrescriptionClient {

    private final WebClient web;

    public PrescriptionClient(
            @Value("${prescription.service.url:http://prescription-service:8080}")
            String baseUrl,
            WebClient.Builder builder
    ) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    /**
     * Restituisce la lista degli UUID dei pazienti presi in carico dal medico.
     * GET /api/v1/doctors/{doctorId}/patients
     * Propaga il JWT dell'utente corrente verso prescription-service.
     */
    public List<UUID> patientsOfDoctor(UUID doctorId) {
        String jwt = currentJwt();
        if (jwt == null) {
            // Qui puoi decidere: lancia eccezione esplicita o fai fallback.
            throw new IllegalStateException("JWT mancante nel SecurityContext: impossibile chiamare prescription-service");
        }

        return web.get()
                .uri("/api/v1/doctors/{id}/patients", doctorId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .bodyToFlux(UUID.class)
                .collectList()
                .block();   // Bloccante per semplicità (sei già in thread servlet)
    }

    /* -------- Utility: recupera il token corrente -------- */
    private String currentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }
}
