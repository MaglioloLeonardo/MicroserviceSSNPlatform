package tassproject.pharmaservice;

import tassproject.pharmaservice.ActiveIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActiveIngredientRepository extends JpaRepository<ActiveIngredient, UUID> {
}