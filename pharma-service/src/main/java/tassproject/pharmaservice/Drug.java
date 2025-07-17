package tassproject.pharmaservice;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "drugs", schema = "pharma_service")
@Data                       // genera getter/setter per tutti i campi
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drug {

    /* ---------- PK ---------- */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /* ---------- Colonne semplici ---------- */
    @Column(name = "nome_commerciale")
    private String nomeCommerciale;

    private String produttore;

    @Column(name = "modo_somministrazione")
    private String modoSomministrazione;

    private BigDecimal prezzo;

    /* ---------- Relazione con ActiveIngredient ---------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principio_attivo_id")
    private ActiveIngredient principioAttivo;

    /* ------------------------------------------------------------------
       ALIAS per compatibilità: getActiveIngredient / setActiveIngredient
       ------------------------------------------------------------------ */

    /**
     * Alias di comodo per evitare LazyInitializationException
     * quando il codice legacy chiama getActiveIngredient().
     */
    public ActiveIngredient getActiveIngredient() {
        return this.principioAttivo;
    }

    /**
     * Alias corrispondente al setter.
     */
    public void setActiveIngredient(ActiveIngredient activeIngredient) {
        this.principioAttivo = activeIngredient;
    }
}
