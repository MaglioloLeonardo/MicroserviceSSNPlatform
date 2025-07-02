package tassproject.prescriptionservice;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false, columnDefinition = "uuid")
    private Prescription prescription;

    @Column(name = "drug_id", nullable = false, columnDefinition = "uuid")
    private UUID drugId;

    @Column(name = "active_ingredient", nullable = false)
    private String activeIngredient; // Es: "Paracetamolo"

    @Column(nullable = false)
    private String dosage; // Es: "500mg"

    @Column(nullable = false)
    private int quantity; // Numero confezioni o unità

    // --- Costruttori ---
    protected PrescriptionItem() {
        // JPA only
    }

    public PrescriptionItem(UUID drugId, String activeIngredient, String dosage, int quantity) {
        this.id = UUID.randomUUID();
        this.drugId = drugId;
        this.activeIngredient = activeIngredient;
        this.dosage = dosage;
        this.quantity = quantity;
    }

    // --- Getter e Setter ---
    public UUID getId() {
        return id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public UUID getDrugId() {
        return drugId;
    }

    public void setDrugId(UUID drugId) {
        this.drugId = drugId;
    }

    public String getActiveIngredient() {
        return activeIngredient;
    }

    public void setActiveIngredient(String activeIngredient) {
        this.activeIngredient = activeIngredient;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // --- Utility per frontend/DTO ---
    public String getDescrizionePrincipioAttivo() {
        return activeIngredient + " " + dosage;
    }

    // --- equals/hashCode opzionali per correttezza in collezioni ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrescriptionItem)) return false;
        PrescriptionItem that = (PrescriptionItem) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
