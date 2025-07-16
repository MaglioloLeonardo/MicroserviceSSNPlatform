package tassproject.pharmaservice;

import  tassproject.pharmaservice.ActiveIngredientResponse;
import  tassproject.pharmaservice.DrugResponse;
import  tassproject.pharmaservice.Drug;
import  tassproject.pharmaservice.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DrugService {

    private final DrugRepository repo;

    public Drug get(UUID id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Drug not found: " + id));
    }

    public List<ActiveIngredientResponse> activeIngredients(UUID drugId) {
        Drug drug = get(drugId);
        return List.of(ActiveIngredientResponse.from(drug.getPrincipioAttivo()));
    }

    public List<DrugResponse> list() {
        return repo.findAll().stream().map(DrugResponse::from).toList();
    }
}