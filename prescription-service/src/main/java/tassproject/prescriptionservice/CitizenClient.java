package tassproject.prescriptionservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tassproject.prescriptionservice.CitizenDTO;

import java.util.UUID;

/** REST‑client minimale verso anagrafica‑service. */
@Component
public class CitizenClient {

    private final WebClient web;

    public CitizenClient(
            @Value("${anagrafica-service.base-url:http://anagrafica-service:8080}")
            String baseUrl) {
        this.web = WebClient.builder().baseUrl(baseUrl).build();
    }

    public CitizenDTO findById(UUID id) {
        return web.get()
                .uri("/api/v1/citizens/{id}", id)
                .retrieve()
                .bodyToMono(CitizenDTO.class)
                .block();
    }
}