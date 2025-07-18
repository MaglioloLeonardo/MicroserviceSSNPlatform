package tassproject.anagraficaservice;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "doctors", schema = "anagrafica_service")
public class Doctor {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private Citizen citizen;

    private String specializzazione;

    protected Doctor() {}

    public Doctor(Citizen citizen, String specializzazione) {
        this.citizen          = citizen;
        this.specializzazione = specializzazione;
    }

    /* —— Getters —— */
    public UUID getId()                   { return id; }
    public Citizen getCitizen()           { return citizen; }
    public String getSpecializzazione()   { return specializzazione; }
}
