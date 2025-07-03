package tassproject.prescriptionservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tassproject.prescriptionservice.PrescriptionApplicationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PrescriptionController {

    private final PrescriptionApplicationService service;

    // Costruttore esplicito per l’injection del service
    public PrescriptionController(PrescriptionApplicationService service) {
        this.service = service;
    }

    @PostMapping("/prescriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponse create(@Valid @RequestBody CreatePrescriptionRequest request) {
        return service.create(request);
    }

    @GetMapping("/prescriptions/{id}")
    public PrescriptionResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/patients/{patientId}/prescriptions/active")
    public List<PrescriptionResponse> findActive(@PathVariable UUID patientId) {
        return service.findActiveByPatientId(patientId);
    }

    @PutMapping("/prescriptions/{id}/status")
    public PrescriptionResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePrescriptionStatusRequest body) {
        return service.updateStatus(id, body.newStatus());
    }
}
