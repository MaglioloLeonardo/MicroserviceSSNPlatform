// inventory-service/src/main/java/tassproject/inventoryservice/repository/InventoryItemRepository.java
package tassproject.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.inventoryservice.InventoryItem;

import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {}
