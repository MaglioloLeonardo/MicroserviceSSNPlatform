package tassproject.prescriptionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tassproject.prescriptionservice.Prescription;

import java.util.List;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    /* ------------- QUERY GIÀ PRESENTI ------------- */

    List<Prescription> findByDoctorId(UUID doctorId);

    @Query("select distinct p.patientId from Prescription p where p.doctorId = :doctorId")
    List<UUID> findDistinctPatientIdsByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("select distinct p.doctorId from Prescription p where p.patientId = :patientId")
    List<UUID> findDistinctDoctorIdsByPatientId(@Param("patientId") UUID patientId);

    /* ------------- NUOVA UTILITY PER SECURITY ----- */

    boolean existsByDoctorIdAndPatientId(UUID doctorId, UUID patientId);
}
