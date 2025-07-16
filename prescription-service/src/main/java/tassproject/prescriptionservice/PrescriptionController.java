package tassproject.prescriptionservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tassproject.prescriptionservice.CitizenDTO;
import tassproject.prescriptionservice.CitizenClient;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class PrescriptionController {

    private final PrescriptionApplicationService service;
    private final AuthorizationService            authz;
    private final CitizenClient                   citizens;   // 🆕

    public PrescriptionController(PrescriptionApplicationService service,
                                  AuthorizationService authz,
                                  CitizenClient citizens) {   // 🆕
        this.service  = service;
        this.authz    = authz;
        this.citizens = citizens;
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

    /* ------------------------------ QUERY UUID ----------------------- */
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

    /* ----------------------- NUOVO: DETTAGLI PAZIENTI ---------------- */
    @GetMapping("/doctors/{doctorId}/patients/details")
    public List<CitizenDTO> patientDetails(@PathVariable UUID doctorId) {
        authz.assertCanAccessDoctor(doctorId);
        return service.getPatientsByDoctor(doctorId).stream()
                .map(citizens::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}