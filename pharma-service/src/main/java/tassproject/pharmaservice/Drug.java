package tassproject.pharmaservice;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "drugs", schema = "pharma_service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drug {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "nome_commerciale")
    private String nomeCommerciale;

    private String produttore;

    @Column(name = "modo_somministrazione")
    private String modoSomministrazione;

    private BigDecimal prezzo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principio_attivo_id")
    private ActiveIngredient principioAttivo;
}