package tassproject.pharmaservice;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DrugRepository extends JpaRepository<Drug, UUID> {

    /**
     * Carica il farmaco insieme al relativo principio attivo.
     * NOTA: il nome nell’EntityGraph deve corrispondere al
     * campo presente in `Drug` (principioAttivo).
     */
    @EntityGraph(attributePaths = "principioAttivo")
    Optional<Drug> findWithActiveIngredientById(UUID id);
}
