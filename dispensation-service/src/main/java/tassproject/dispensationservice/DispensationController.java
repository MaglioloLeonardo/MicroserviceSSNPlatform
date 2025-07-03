package tassproject.dispensationservice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DispensationController {

    private final DispensationApplicationService service;

    // Costruttore esplicito con service iniettato da Spring
    public DispensationController(DispensationApplicationService service) {
        this.service = service;
    }

    @PostMapping("/dispensations")
    @ResponseStatus(HttpStatus.CREATED)
    public DispensationResponse dispense(@Valid @RequestBody CreateDispensationRequest request) {
        return service.dispense(request);
    }

    @GetMapping("/prescriptions/{prescriptionId}/dispensations")
    public List<DispensationResponse> list(@PathVariable UUID prescriptionId) {
        return service.listByPrescription(prescriptionId);
    }
}
