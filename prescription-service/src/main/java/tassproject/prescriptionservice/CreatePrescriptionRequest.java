package tassproject.prescriptionservice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreatePrescriptionRequest(
        @NotNull UUID doctorId,
        @NotNull UUID patientId,
        String therapyDuration,
        boolean exemption,
        @Size(min = 1) List<@NotNull PrescriptionItemDto> items
) {}