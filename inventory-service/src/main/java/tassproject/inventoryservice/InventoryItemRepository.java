package tassproject.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.inventoryservice.InventoryItem;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    /* Cerca la riga di magazzino per drugId */
    Optional<InventoryItem> findByDrugId(UUID drugId);
}
