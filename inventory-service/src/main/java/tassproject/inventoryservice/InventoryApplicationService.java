package tassproject.inventoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.inventoryservice.AvailabilityResponse;
import tassproject.inventoryservice.repository.InventoryItemRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryApplicationService {
    private final InventoryItemRepository repo;

    public AvailabilityResponse checkAvailability(UUID drugId) {
        var qty = repo.findAll().stream()
                .filter(i -> i.getDrugId().equals(drugId))
                .mapToInt(i -> i.getAvailableQuantity())
                .sum();
        return new AvailabilityResponse(drugId, qty);
    }
}