package tassproject.anagraficaservice;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "citizens", schema = "anagrafica_service")
public class Citizen {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, length = 16)
    private String cf;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cognome;

    @Column(name = "data_nascita", nullable = false)
    private LocalDate dataNascita;

    @Column(name = "luogo_nascita", length = 100)
    private String luogoNascita;

    @Column(name = "citta_residenza", length = 100)
    private String cittaResidenza;

    @Column(name = "tipo_utente", nullable = false, length = 16)
    private String tipoUtente;

    protected Citizen() {}

    public Citizen(UUID id,
                   String cf,
                   String nome,
                   String cognome,
                   LocalDate dataNascita,
                   String luogoNascita,
                   String cittaResidenza,
                   String tipoUtente) {
        this.id             = id;
        this.cf             = cf;
        this.nome           = nome;
        this.cognome        = cognome;
        this.dataNascita    = dataNascita;
        this.luogoNascita   = luogoNascita;
        this.cittaResidenza = cittaResidenza;
        this.tipoUtente     = tipoUtente;
    }

    /* —— Getters & Setters —— */
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCf() { return cf; }
    public void setCf(String cf) { this.cf = cf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }

    public String getLuogoNascita() { return luogoNascita; }
    public void setLuogoNascita(String luogoNascita) { this.luogoNascita = luogoNascita; }

    public String getCittaResidenza() { return cittaResidenza; }
    public void setCittaResidenza(String cittaResidenza) { this.cittaResidenza = cittaResidenza; }

    public String getTipoUtente() { return tipoUtente; }
    public void setTipoUtente(String tipoUtente) { this.tipoUtente = tipoUtente; }
}
