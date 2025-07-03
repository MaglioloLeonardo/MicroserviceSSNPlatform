package tassproject.inventoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tassproject.inventoryservice.InventoryApplicationService;
import tassproject.inventoryservice.AvailabilityResponse;

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
}
