package tassproject.inventoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryApplicationService service;

    @GetMapping("/inventory/{drugId}/availability")
    public AvailabilityResponse availability(@PathVariable UUID drugId) {
        return service.checkAvailability(drugId);
    }

    @PostMapping("/inventory/items/{drugId}/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserve(@PathVariable UUID drugId, @RequestBody ReserveInventoryRequest body) {
        service.reserve(drugId, body.quantity(), body.reason());
    }

    /* ===== NUOVO: inventario di una farmacia dato il farmacista (frontend passa l’ID farmacia) */
    @GetMapping("/pharmacists/{pharmacistId}/pharmacies/{pharmacyId}/inventory")
    public List<InventoryItemResponse> inventoryByPharmacy(
            @PathVariable UUID pharmacistId,
            @PathVariable UUID pharmacyId) {
        // Eventuale authorization layer qui …
        return service.listByPharmacy(pharmacyId);
    }
}
