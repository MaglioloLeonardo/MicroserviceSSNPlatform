package tassproject.dispensationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.dispensationservice.Dispensation;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface DispensationRepository extends JpaRepository<Dispensation, UUID> {

    /* 4) Ordini di un farmacista */
    List<Dispensation> findByDispensedBy(UUID pharmacistId);

    /* supporto a 2) / 6)  */
    List<Dispensation> findByPrescriptionIdIn(Collection<UUID> prescriptionIds);
}
