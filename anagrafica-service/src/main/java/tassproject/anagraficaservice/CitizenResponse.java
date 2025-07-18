package tassproject.anagraficaservice;

import tassproject.anagraficaservice.Citizen;

import java.time.LocalDate;
import java.util.UUID;

public record CitizenResponse(
        UUID id,
        String cf,
        String nome,
        String cognome,
        LocalDate dataNascita,
        String luogoNascita,
        String cittaResidenza,
        String tipoUtente
) {
    public static CitizenResponse from(Citizen c) {
        return new CitizenResponse(
                c.getId(),
                c.getCf(),
                c.getNome(),
                c.getCognome(),
                c.getDataNascita(),
                c.getLuogoNascita(),
                c.getCittaResidenza(),
                c.getTipoUtente()
        );
    }
}
