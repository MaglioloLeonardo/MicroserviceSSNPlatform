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

    @Column(name = "active_ingredient_id", nullable = false, columnDefinition = "uuid")
    private UUID activeIngredientId;

    @Column(name = "active_ingredient", nullable = false)
    private String activeIngredient;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private int quantity;

    protected PrescriptionItem() {
        // per JPA
    }

    public PrescriptionItem(UUID drugId,
                            UUID activeIngredientId,
                            String activeIngredient,
                            String dosage,
                            int quantity) {
        this.id = UUID.randomUUID();
        this.drugId = drugId;
        this.activeIngredientId = activeIngredientId;
        this.activeIngredient = activeIngredient;
        this.dosage = dosage;
        this.quantity = quantity;
    }

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

    public UUID getActiveIngredientId() {
        return activeIngredientId;
    }

    public void setActiveIngredientId(UUID activeIngredientId) {
        this.activeIngredientId = activeIngredientId;
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

    public String getDescrizionePrincipioAttivo() {
        return activeIngredient + " " + dosage;
    }

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
