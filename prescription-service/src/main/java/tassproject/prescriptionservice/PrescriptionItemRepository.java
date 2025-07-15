package tassproject.prescriptionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tassproject.prescriptionservice.PrescriptionItem;

import java.util.List;
import java.util.UUID;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, UUID> {

    /* farmaci (distinct) acquistati da un paziente */
    @Query("""
            select distinct i.drugId
            from PrescriptionItem i
            where i.prescription.patientId = :patientId
          """)
    List<UUID> findDistinctDrugIdsByPatientId(@Param("patientId") UUID patientId);
}
