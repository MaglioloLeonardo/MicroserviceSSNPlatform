package tassproject.anagraficaservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class PrescriptionClient {

    private final WebClient web;

    public PrescriptionClient(@Value("${prescription.service.url:http://prescription-service:8080}")
                              String baseUrl,
                              WebClient.Builder builder) {
        this.web = builder.baseUrl(baseUrl).build();
    }

    /**
     * Restituisce la lista degli UUID dei pazienti presi in carico dal medico.
     * GET  /api/v1/doctors/{doctorId}/patients
     */
    public List<UUID> patientsOfDoctor(UUID doctorId) {
        return web.get()
                .uri("/api/v1/doctors/{id}/patients", doctorId)
                .retrieve()
                .bodyToFlux(UUID.class)
                .collectList()
                .block();     // ⇢ bloccante per semplicità
    }
}
