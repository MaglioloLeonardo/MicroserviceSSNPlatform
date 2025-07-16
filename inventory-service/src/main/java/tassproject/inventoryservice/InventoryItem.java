package tassproject.inventoryservice;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "drug_id", nullable = false, columnDefinition = "uuid")
    private UUID drugId;

    @Column(name = "pharmacy_id", nullable = false, columnDefinition = "uuid")   // 🆕
    private UUID pharmacyId;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "last_updated", nullable = false)
    private OffsetDateTime lastUpdated;

    protected InventoryItem() {}

    public InventoryItem(UUID id, UUID drugId, UUID pharmacyId, int availableQuantity, OffsetDateTime lastUpdated) {
        this.id = id;
        this.drugId = drugId;
        this.pharmacyId = pharmacyId;
        this.availableQuantity = availableQuantity;
        this.lastUpdated = lastUpdated;
    }

    // -------- getters/setters --------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDrugId() { return drugId; }
    public void setDrugId(UUID drugId) { this.drugId = drugId; }

    public UUID getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(UUID pharmacyId) { this.pharmacyId = pharmacyId; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public OffsetDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(OffsetDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
