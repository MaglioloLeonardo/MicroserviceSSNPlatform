package tassproject.pharmaservice;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "active_ingredients", schema = "pharma_service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveIngredient {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String nome;

    private String descrizione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "famiglia_id")
    private PharmaFamily famiglia;
}