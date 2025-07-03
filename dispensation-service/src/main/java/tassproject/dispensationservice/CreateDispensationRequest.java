package tassproject.dispensationservice;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateDispensationRequest(@NotNull UUID prescriptionId, @NotNull UUID dispensedBy) {}