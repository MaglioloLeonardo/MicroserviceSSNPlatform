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

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "last_updated", nullable = false)
    private OffsetDateTime lastUpdated;

    // **Costruttore vuoto richiesto da JPA**
    public InventoryItem() {}

    // (Opzionale) costruttore full-args
    public InventoryItem(UUID id, UUID drugId, int availableQuantity, OffsetDateTime lastUpdated) {
        this.id = id;
        this.drugId = drugId;
        this.availableQuantity = availableQuantity;
        this.lastUpdated = lastUpdated;
    }

    // --- Getter e Setter ---
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDrugId() {
        return drugId;
    }
    public void setDrugId(UUID drugId) {
        this.drugId = drugId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
