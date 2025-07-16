package tassproject.pharmaservice;

import tassproject.pharmaservice.ActiveIngredientResponse;
import tassproject.pharmaservice.ActiveIngredient;
import tassproject.pharmaservice.ActiveIngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActiveIngredientService {

    private final ActiveIngredientRepository repo;

    public ActiveIngredient get(UUID id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Active ingredient not found: " + id));
    }

    public List<ActiveIngredientResponse> listAll() {
        return repo.findAll().stream().map(ActiveIngredientResponse::from).toList();
    }
}

