package tassproject.pharmaservice;

import tassproject.pharmaservice.Drug;

import java.math.BigDecimal;
import java.util.UUID;

public record DrugResponse(UUID id, String nomeCommerciale, String produttore,
                           String modoSomministrazione, BigDecimal prezzo, UUID principioAttivoId) {
    public static DrugResponse from(Drug d) {
        return new DrugResponse(d.getId(), d.getNomeCommerciale(), d.getProduttore(),
                d.getModoSomministrazione(), d.getPrezzo(),
                d.getPrincipioAttivo() != null ? d.getPrincipioAttivo().getId() : null);
    }
}