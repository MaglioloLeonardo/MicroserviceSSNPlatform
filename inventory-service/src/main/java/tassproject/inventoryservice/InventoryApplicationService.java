package tassproject.inventoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.inventoryservice.repository.InventoryItemRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryApplicationService {

    private final InventoryItemRepository repo;

    /* ────────────── lettura ────────────── */

    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(UUID drugId) {
        int qty = repo.findByDrugId(drugId)
                .map(InventoryItem::getAvailableQuantity)
                .orElse(0);
        return new AvailabilityResponse(drugId, qty);
    }

    /* ───────────── scrittura ───────────── */

    public void reserve(UUID drugId, int quantity, String reason) {

        // 1) La riga di magazzino DEVE esistere: se manca → 400
        var row = repo.findByDrugId(drugId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Il farmaco %s non è presente a magazzino".formatted(drugId)));

        // 2) Controllo stock sufficiente
        if (row.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Scorte insufficienti per la prenotazione");
        }

        // 3) Aggiornamento quantità
        row.setAvailableQuantity(row.getAvailableQuantity() - quantity);
        row.setLastUpdated(OffsetDateTime.now());
        repo.save(row);

        // (eventuale publish Rabbit / log, se serve)
    }
}
