package tassproject.anagraficaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tassproject.anagraficaservice.CitizenResponse;
import tassproject.anagraficaservice.AuthorizationService;
import tassproject.anagraficaservice.AnagraficaApplicationService;
import tassproject.anagraficaservice.PrescriptionClient;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CitizenController {

    private final AnagraficaApplicationService service;
    private final AuthorizationService         authz;
    private final PrescriptionClient           prescriptions;

    /* ——— 1) La mia anagrafica ——— */
    @GetMapping("/citizens/me")
    public CitizenResponse me() {
        UUID me = authz.userId();
        return service.getCitizen(me);
    }

    /* ——— 2) Singolo cittadino ——— */
    @GetMapping("/citizens/{citizenId}")
    public CitizenResponse citizen(@PathVariable UUID citizenId) {
        authz.assertCanAccessCitizen(citizenId);
        return service.getCitizen(citizenId);
    }

    /* ——— 3) Tutti i pazienti di un medico ——— */
    @GetMapping("/doctors/{doctorId}/patients")
    public List<CitizenResponse> patientsOfDoctor(@PathVariable UUID doctorId) {
        authz.assertCanAccessDoctorPatients(doctorId);
        List<UUID> ids = prescriptions.patientsOfDoctor(doctorId);
        return service.listCitizens(ids);
    }
}
