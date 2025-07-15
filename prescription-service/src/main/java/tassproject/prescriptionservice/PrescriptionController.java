package tassproject.prescriptionservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tassproject.prescriptionservice.AuthorizationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PrescriptionController {

    private final PrescriptionApplicationService service;
    private final AuthorizationService authz;

    public PrescriptionController(PrescriptionApplicationService service,
                                  AuthorizationService authz) {
        this.service = service;
        this.authz   = authz;
    }

    /* ------------------------------ CRUD ESISTENTI -------------------- */

    @PostMapping("/prescriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponse create(@Valid @RequestBody CreatePrescriptionRequest request) {
        authz.assertCanAccessDoctor(request.doctorId());
        authz.assertCanAccessPatient(request.patientId());
        return service.create(request);
    }

    @GetMapping("/prescriptions/{id}")
    public PrescriptionResponse findById(@PathVariable UUID id) {
        var resp = service.findById(id);
        authz.assertCanAccessDoctor(resp.doctorId());
        authz.assertCanAccessPatient(resp.patientId());
        return resp;
    }

    @GetMapping("/patients/{patientId}/prescriptions/active")
    public List<PrescriptionResponse> findActive(@PathVariable UUID patientId) {
        authz.assertCanAccessPatient(patientId);
        return service.findActiveByPatientId(patientId);
    }

    @PutMapping("/prescriptions/{id}/status")
    public PrescriptionResponse updateStatus(@PathVariable UUID id,
                                             @Valid @RequestBody UpdatePrescriptionStatusRequest body) {
        var resp = service.findById(id);
        authz.assertCanAccessDoctor(resp.doctorId());
        return service.updateStatus(id, body.newStatus());
    }

    /* ------------------------------ NUOVI ENDPOINT -------------------- */

    @GetMapping("/doctors/{doctorId}/patients")
    public List<UUID> patientsOfDoctor(@PathVariable UUID doctorId) {
        authz.assertCanAccessDoctor(doctorId);
        return service.getPatientsByDoctor(doctorId);
    }

    @GetMapping("/patients/{patientId}/doctors")
    public List<UUID> doctorsOfPatient(@PathVariable UUID patientId) {
        authz.assertCanAccessPatient(patientId);
        return service.getDoctorsByPatient(patientId);
    }

    @GetMapping("/doctors/{doctorId}/prescriptions")
    public List<PrescriptionResponse> prescriptionsByDoctor(@PathVariable UUID doctorId) {
        authz.assertCanAccessDoctor(doctorId);
        return service.findByDoctor(doctorId);
    }

    @GetMapping("/patients/{patientId}/drugs")
    public List<UUID> drugsByPatient(@PathVariable UUID patientId) {
        authz.assertCanAccessPatient(patientId);
        return service.getDrugIdsByPatient(patientId);
    }
}
