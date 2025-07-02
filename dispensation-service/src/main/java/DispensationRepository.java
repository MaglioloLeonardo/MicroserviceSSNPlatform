package tassproject.dispensationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.dispensationservice.Dispensation;

import java.util.UUID;

public interface DispensationRepository extends JpaRepository<Dispensation, UUID> {
}
