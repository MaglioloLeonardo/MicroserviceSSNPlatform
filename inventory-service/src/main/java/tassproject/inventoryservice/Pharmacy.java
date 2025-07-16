package tassproject.inventoryservice;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pharmacies")
public class Pharmacy {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String nome;
    private String indirizzo;

    protected Pharmacy() {}
    public Pharmacy(UUID id, String nome, String indirizzo) {
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getIndirizzo() { return indirizzo; }
}