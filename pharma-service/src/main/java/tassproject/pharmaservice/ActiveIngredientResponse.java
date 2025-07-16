package tassproject.pharmaservice;

import tassproject.pharmaservice.ActiveIngredient;

import java.util.UUID;

public record ActiveIngredientResponse(UUID id, String nome, String descrizione, UUID famigliaId) {
    public static ActiveIngredientResponse from(ActiveIngredient a) {
        return new ActiveIngredientResponse(a.getId(), a.getNome(), a.getDescrizione(),
                a.getFamiglia() != null ? a.getFamiglia().getId() : null);
    }
}