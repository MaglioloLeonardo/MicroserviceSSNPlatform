package tassproject.inventoryservice;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryItemResponse(UUID id, UUID drugId, int availableQuantity, OffsetDateTime lastUpdated) {
    public static InventoryItemResponse from(InventoryItem i) {
        return new InventoryItemResponse(i.getId(), i.getDrugId(), i.getAvailableQuantity(), i.getLastUpdated());
    }
}