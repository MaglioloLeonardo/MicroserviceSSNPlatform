package tassproject.pharmaservice;

import tassproject.pharmaservice.PharmaFamily;

import java.util.UUID;

public record PharmaFamilyResponse(UUID id, String nome, String descrizione) {
    public static PharmaFamilyResponse from(PharmaFamily f) {
        return new PharmaFamilyResponse(f.getId(), f.getNome(), f.getDescrizione());
    }
}
