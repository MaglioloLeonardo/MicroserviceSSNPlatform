package tassproject.anagraficaservice;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "patients", schema = "anagrafica_service")
public class Patient {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private Citizen citizen;

    private BigDecimal peso;    // kg
    private BigDecimal altezza; // cm

    protected Patient() {}

    public Patient(Citizen citizen, BigDecimal peso, BigDecimal altezza) {
        this.citizen = citizen;
        this.peso    = peso;
        this.altezza = altezza;
    }

    /* —— Getters —— */
    public UUID getId()            { return id; }
    public Citizen getCitizen()    { return citizen; }
    public BigDecimal getPeso()    { return peso; }
    public BigDecimal getAltezza() { return altezza; }
}
