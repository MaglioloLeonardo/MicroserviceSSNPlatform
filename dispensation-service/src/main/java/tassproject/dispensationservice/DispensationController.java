package tassproject.dispensationservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tassproject.dispensationservice.client.PrescriptionClient;
import tassproject.dispensationservice.security.AuthorizationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DispensationController {

    private final DispensationApplicationService service;
    private final AuthorizationService           authz;
    private final PrescriptionClient             prescriptions;   // ← nuovo

    public DispensationController(DispensationApplicationService service,
                                  AuthorizationService authz,
                                  PrescriptionClient prescriptions) {
        this.service        = service;
        this.authz          = authz;
        this.prescriptions  = prescriptions;
    }

    /* ------------------------------ CREA ------------------------------ */
    @PostMapping("/dispensations")
    @ResponseStatus(HttpStatus.CREATED)
    public DispensationResponse dispense(@Valid @RequestBody CreateDispensationRequest body) {
        authz.assertCanAccessPharmacist(body.dispensedBy());
        return service.dispense(body);
    }

    /* -------------------------- QUERY PRESCRIZIONE -------------------- */
    @GetMapping("/prescriptions/{prescriptionId}/dispensations")
    public List<DispensationResponse> byPrescription(@PathVariable UUID prescriptionId) {
        var dto = prescriptions.getPrescription(prescriptionId);
        if (dto == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescrizione inesistente");

        authz.assertCanAccessPatient(dto.patientId());
        return service.listByPrescription(prescriptionId);
    }

    /* ------------------------------ NUOVI ENDPOINT -------------------- */
    @GetMapping("/pharmacists/{pharmacistId}/dispensations")
    public List<DispensationResponse> byPharmacist(@PathVariable UUID pharmacistId) {
        authz.assertCanAccessPharmacist(pharmacistId);
        return service.listByPharmacist(pharmacistId);
    }

    @GetMapping("/pharmacists/{pharmacistId}/patients")
    public List<UUID> patientsOfPharmacist(@PathVariable UUID pharmacistId) {
        authz.assertCanAccessPharmacist(pharmacistId);
        return service.patientsByPharmacist(pharmacistId);
    }

    @GetMapping("/patients/{patientId}/pharmacists")
    public List<UUID> pharmacistsOfPatient(@PathVariable UUID patientId) {
        authz.assertCanAccessPatient(patientId);
        return service.pharmacistsByPatient(patientId);
    }
}
