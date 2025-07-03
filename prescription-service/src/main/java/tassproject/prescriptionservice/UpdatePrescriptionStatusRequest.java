package tassproject.prescriptionservice;

import jakarta.validation.constraints.NotNull;
import tassproject.prescriptionservice.Prescription;

public record UpdatePrescriptionStatusRequest(@NotNull Prescription.Status newStatus) {}