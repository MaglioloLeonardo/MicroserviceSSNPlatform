package tassproject.prescriptionservice;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PrescriptionItemDto(
        @NotNull UUID drugId,
        @NotNull UUID activeIngredientId,
        @NotBlank String activeIngredient,
        @NotBlank String dosage,
        @Min(1) int quantity
) {}
