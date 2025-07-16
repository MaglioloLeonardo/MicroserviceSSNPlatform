package tassproject.pharmaservice;

import tassproject.pharmaservice.PharmaFamilyResponse;
import tassproject.pharmaservice.PharmaFamily;
import tassproject.pharmaservice.PharmaFamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharma-families")
@RequiredArgsConstructor
public class PharmaFamilyController {

    private final PharmaFamilyRepository repo;

    @GetMapping
    public List<PharmaFamilyResponse> list() {
        return repo.findAll().stream().map(PharmaFamilyResponse::from).toList();
    }
}