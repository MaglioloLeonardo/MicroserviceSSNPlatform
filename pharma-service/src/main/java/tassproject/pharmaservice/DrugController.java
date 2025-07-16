package tassproject.pharmaservice;

import tassproject.pharmaservice.ActiveIngredientResponse;
import tassproject.pharmaservice.DrugResponse;
import tassproject.pharmaservice.DrugService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService service;

    @GetMapping
    public List<DrugResponse> listDrugs() {
        return service.list();
    }

    @GetMapping("/{id}/active-ingredients")
    public List<ActiveIngredientResponse> activeIngredients(@PathVariable UUID id) {
        return service.activeIngredients(id);
    }
}

