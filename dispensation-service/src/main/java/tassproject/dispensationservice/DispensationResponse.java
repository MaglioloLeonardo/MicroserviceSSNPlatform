package tassproject.dispensationservice;

import tassproject.dispensationservice.Dispensation;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DispensationResponse(UUID id, UUID prescriptionId, OffsetDateTime dispensedAt, UUID dispensedBy, Dispensation.Status status) {
    public static DispensationResponse from(Dispensation d) {
        return new DispensationResponse(d.getId(), d.getPrescriptionId(), d.getDispensedAt(), d.getDispensedBy(), d.getStatus());
    }
}