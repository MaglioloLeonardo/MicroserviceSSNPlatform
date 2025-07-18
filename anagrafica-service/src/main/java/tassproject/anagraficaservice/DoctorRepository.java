package tassproject.anagraficaservice;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.anagraficaservice.Doctor;

import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
}
