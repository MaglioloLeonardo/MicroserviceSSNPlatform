package tassproject.prescriptionservice;

import java.time.LocalDate;
import java.util.UUID;

/** DTO ri‑usabile inter‑service con i campi anagrafici essenziali. */
public record CitizenDTO(
        UUID id,
        String cf,
        String nome,
        String cognome,
        LocalDate dataNascita,
        String luogoNascita,
        String cittaResidenza) {
}