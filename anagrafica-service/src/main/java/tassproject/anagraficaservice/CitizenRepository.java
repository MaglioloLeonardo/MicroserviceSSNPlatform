package tassproject.anagraficaservice;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.anagraficaservice.Citizen;

import java.util.UUID;

public interface CitizenRepository extends JpaRepository<Citizen, UUID> {
}
