// PrescriptionItemRepository.java
package tassproject.prescriptionservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.prescriptionservice.PrescriptionItem;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, UUID> { }
