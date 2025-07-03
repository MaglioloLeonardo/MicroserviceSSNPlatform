package tassproject.inventoryservice;

import java.util.UUID;

public record AvailabilityResponse(UUID drugId, int quantity) {}