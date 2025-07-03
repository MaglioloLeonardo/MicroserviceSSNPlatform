package tassproject.prescriptionservice;

import tassproject.prescriptionservice.Prescription;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PrescriptionResponse(
        UUID id,
        UUID doctorId,
        UUID patientId,
        OffsetDateTime issuedAt,
        Prescription.Status status,
        boolean exemption,
        String therapyDuration,
        List<PrescriptionItemDto> items
) {
    public static PrescriptionResponse from(Prescription p) {
        return new PrescriptionResponse(
                p.getId(),
                p.getDoctorId(),
                p.getPatientId(),
                p.getIssuedAt(),
                p.getStatus(),
                p.isExemption(),
                p.getTherapyDuration(),
                p.getItems().stream()
                        .map(it -> new PrescriptionItemDto(it.getDrugId(), it.getActiveIngredient(), it.getDosage(), it.getQuantity()))
                        .toList()
        );
    }
}
