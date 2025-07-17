package tassproject.pharmaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService service;

    /*----------- GET /api/v1/drugs -----------*/
    @GetMapping
    public Flux<DrugResponse> listDrugs() {
        return service.list();
    }

    /*------ GET /api/v1/drugs/{id} -----------*/
    @GetMapping("/{id}")
    public Mono<DrugResponse> getDrug(@PathVariable UUID id) {
        return service.get(id);
    }

    /*-- GET /api/v1/drugs/{id}/active-ingredients --*/
    @GetMapping("/{id}/active-ingredients")
    public Flux<ActiveIngredientResponse> activeIngredients(@PathVariable UUID id) {
        return service.activeIngredients(id);
    }
}
