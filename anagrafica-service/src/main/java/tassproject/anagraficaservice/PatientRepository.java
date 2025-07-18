package tassproject.anagraficaservice;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.anagraficaservice.Patient;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
