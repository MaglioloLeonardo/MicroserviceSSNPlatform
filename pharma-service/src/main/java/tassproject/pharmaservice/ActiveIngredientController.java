package tassproject.pharmaservice;

import tassproject.pharmaservice.ActiveIngredientResponse;
import tassproject.pharmaservice.ActiveIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/active-ingredients")
@RequiredArgsConstructor
public class ActiveIngredientController {

    private final ActiveIngredientService service;

    @GetMapping
    public List<ActiveIngredientResponse> list() {
        return service.listAll();
    }
}
