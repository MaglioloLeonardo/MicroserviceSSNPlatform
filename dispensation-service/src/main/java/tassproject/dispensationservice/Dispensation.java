package tassproject.dispensationservice;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "dispensations")
public class Dispensation {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    // Riferimento alla Prescription via UUID, non l’entità JPA di un altro microservizio
    @Column(name = "prescription_id", nullable = false, columnDefinition = "uuid")
    private UUID prescriptionId;

    @Column(name = "dispensed_at", nullable = false)
    private OffsetDateTime dispensedAt;

    @Column(name = "dispensed_by", nullable = false, columnDefinition = "uuid")
    private UUID dispensedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        DONE,
        FAILED
    }

    public Dispensation() {}

    public Dispensation(UUID id, UUID prescriptionId, OffsetDateTime dispensedAt, UUID dispensedBy, Status status) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.dispensedAt = dispensedAt;
        this.dispensedBy = dispensedBy;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(UUID prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public OffsetDateTime getDispensedAt() {
        return dispensedAt;
    }

    public void setDispensedAt(OffsetDateTime dispensedAt) {
        this.dispensedAt = dispensedAt;
    }

    public UUID getDispensedBy() {
        return dispensedBy;
    }

    public void setDispensedBy(UUID dispensedBy) {
        this.dispensedBy = dispensedBy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
