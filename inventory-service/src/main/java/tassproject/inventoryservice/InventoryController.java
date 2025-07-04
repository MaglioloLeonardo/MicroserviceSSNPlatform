package tassproject.inventoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    /* ─────────────── nuovo endpoint “reserve” ─────────────── */

    @PostMapping("/inventory/items/{drugId}/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserve(@PathVariable UUID drugId,
                        @RequestBody ReserveInventoryRequest body) {
        service.reserve(drugId, body.quantity(), body.reason());
    }
}
