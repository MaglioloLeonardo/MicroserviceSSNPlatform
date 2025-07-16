package tassproject.inventoryservice;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.inventoryservice.InventoryItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    Optional<InventoryItem> findByDrugId(UUID drugId);
    List<InventoryItem>     findByPharmacyId(UUID pharmacyId); }