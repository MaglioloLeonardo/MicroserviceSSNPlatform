package tassproject.inventoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.inventoryservice.InventoryItemRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryApplicationService {

    private final InventoryItemRepository repo;

    /* ===== lettura ===== */
    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(UUID drugId) {
        int qty = repo.findByDrugId(drugId)
                .map(InventoryItem::getAvailableQuantity)
                .orElse(0);
        return new AvailabilityResponse(drugId, qty);
    }

    /** Elenco completo (DTO) degli articoli di una farmacia. */
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> listByPharmacy(UUID pharmacyId) {
        return repo.findByPharmacyId(pharmacyId).stream()
                .map(InventoryItemResponse::from)
                .toList();
    }

    /* ===== scrittura ===== */
    public void reserve(UUID drugId, int quantity, String reason) {
        var row = repo.findByDrugId(drugId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Il farmaco %s non è presente a magazzino".formatted(drugId)));
        if (row.getAvailableQuantity() < quantity)
            throw new IllegalArgumentException("Scorte insufficienti per la prenotazione");
        row.setAvailableQuantity(row.getAvailableQuantity() - quantity);
        row.setLastUpdated(OffsetDateTime.now());
        repo.save(row);
    }
}