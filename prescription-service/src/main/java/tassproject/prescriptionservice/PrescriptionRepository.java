// PrescriptionRepository.java
package tassproject.prescriptionservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.prescriptionservice.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> { }
