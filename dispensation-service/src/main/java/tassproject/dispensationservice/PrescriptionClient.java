package tassproject.dispensationservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

/** Client REST minimale verso prescription‑service (solo ciò che serve a dispensation‑service). */
@Component
public class PrescriptionClient {

    private final WebClient webClient;

    public PrescriptionClient(
            @Value("${prescription-service.base-url:http://prescription-service:8080}")
            String baseUrl) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /* DTO locale; evita di importare classi di un altro microservizio */
    public record PrescriptionDTO(UUID id, UUID patientId, UUID doctorId) {}

    /** Restituisce i dati minimi di una prescrizione. */
    public PrescriptionDTO getPrescription(UUID id) {
        return webClient.get()
                .uri("/api/v1/prescriptions/{id}", id)
                .retrieve()
                .bodyToMono(PrescriptionDTO.class)
                .block();
    }

    /** Elenco (solo id) di tutte le prescrizioni di un paziente – ci basta per query aggregata. */
    public List<UUID> listPrescriptionIdsByPatient(UUID patientId) {
        return webClient.get()
                .uri("/api/v1/patients/{patientId}/prescriptions", patientId)
                .retrieve()
                .bodyToFlux(PrescriptionDTO.class)
                .map(PrescriptionDTO::id)
                .collectList()
                .block();
    }
}
