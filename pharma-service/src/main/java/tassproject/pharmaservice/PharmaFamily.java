package tassproject.pharmaservice;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pharma_families", schema = "pharma_service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmaFamily {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String nome;

    private String descrizione;
}