package tassproject.inventoryservice;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReserveInventoryRequest(
        @Min(1) int quantity,
        @NotBlank String reason) {}
