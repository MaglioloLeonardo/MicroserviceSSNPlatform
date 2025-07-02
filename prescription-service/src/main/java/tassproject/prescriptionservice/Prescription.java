package tassproject.prescriptionservice;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "doctor_id", nullable = false, columnDefinition = "uuid")
    private UUID doctorId;

    @Column(name = "patient_id", nullable = false, columnDefinition = "uuid")
    private UUID patientId;

    @Column(name = "issued_at", nullable = false)
    private OffsetDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "exemption", nullable = false)
    private boolean exemption; // true = esente ticket, false = non esente

    @Column(name = "therapy_duration")
    private String therapyDuration; // Es: "7 giorni" (mappato direttamente per semplicità)

    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PrescriptionItem> items = new ArrayList<>();

    // --- Enum Stato Ricetta ---
    public enum Status {
        OPEN,
        DISPENSED,
        COMPLETED,
        CANCELLED
    }

    // --- Costruttori ---
    protected Prescription() {} // richiesto da JPA

    public Prescription(UUID id, UUID doctorId, UUID patientId, OffsetDateTime issuedAt, boolean exemption, String therapyDuration, List<PrescriptionItem> items) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.issuedAt = issuedAt;
        this.status = Status.OPEN;
        this.exemption = exemption;
        this.therapyDuration = therapyDuration;
        setItems(items); // associa gli items a questa prescription
    }

    // Factory per nuove prescrizioni
    public static Prescription create(UUID doctorId, UUID patientId, boolean exemption, String therapyDuration, List<PrescriptionItem> items) {
        return new Prescription(UUID.randomUUID(), doctorId, patientId, OffsetDateTime.now(), exemption, therapyDuration, items);
    }

    // --- Metodi dominio ---
    public boolean isOpen() {
        return status == Status.OPEN;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isDispensed() {
        return status == Status.DISPENSED;
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    public void markDispensed() {
        if (!isOpen()) throw new IllegalStateException("Prescription must be OPEN to be dispensed.");
        this.status = Status.DISPENSED;
    }

    public void markCompleted() {
        if (status != Status.DISPENSED) throw new IllegalStateException("Prescription must be DISPENSED to complete.");
        this.status = Status.COMPLETED;
    }

    public void cancel() {
        if (isCompleted()) throw new IllegalStateException("Cannot cancel a completed prescription.");
        this.status = Status.CANCELLED;
    }

    // --- Getter e Setter ---
    public UUID getId() { return id; }
    public UUID getDoctorId() { return doctorId; }
    public UUID getPatientId() { return patientId; }
    public OffsetDateTime getIssuedAt() { return issuedAt; }
    public Status getStatus() { return status; }
    public boolean isExemption() { return exemption; }
    public String getTherapyDuration() { return therapyDuration; }
    public List<PrescriptionItem> getItems() { return items; }

    public void setItems(List<PrescriptionItem> items) {
        this.items.clear();
        if (items != null) {
            for (PrescriptionItem item : items) {
                item.setPrescription(this); // imposta il back-reference
                this.items.add(item);
            }
        }
    }

    // Opzionale: aggiungi utility per frontend
    public String getEsenzioneAsString() {
        return exemption ? "Si" : "No";
    }
}
