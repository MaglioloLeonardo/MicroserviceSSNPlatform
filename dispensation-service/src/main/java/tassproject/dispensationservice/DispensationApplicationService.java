package tassproject.dispensationservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.dispensationservice.client.PrescriptionClient;
import tassproject.dispensationservice.repository.DispensationRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DispensationApplicationService {

    private final DispensationRepository repo;
    private final PrescriptionClient prescriptions;   // ← nuovo client REST
    private final RabbitTemplate rabbitTemplate;

    public DispensationApplicationService(DispensationRepository repo,
                                          PrescriptionClient prescriptions,
                                          RabbitTemplate rabbitTemplate) {
        this.repo           = repo;
        this.prescriptions  = prescriptions;
        this.rabbitTemplate = rabbitTemplate;
    }

    /* ------------------------- CREA ------------------------- */
    public DispensationResponse dispense(CreateDispensationRequest request) {
        var entity = new Dispensation(
                UUID.randomUUID(),
                request.prescriptionId(),
                OffsetDateTime.now(),
                request.dispensedBy(),
                Dispensation.Status.DONE);

        entity = repo.save(entity);
        rabbitTemplate.convertAndSend("dispensation.events",
                "PrescriptionDispensed",
                entity.getPrescriptionId());
        return DispensationResponse.from(entity);
    }

    /* --------------------- QUERY BASE ----------------------- */
    @Transactional(readOnly = true)
    public List<DispensationResponse> listByPrescription(UUID prescriptionId) {
        return repo.findByPrescriptionIdIn(List.of(prescriptionId))
                .stream().map(DispensationResponse::from).toList();
    }

    /* 4) ordini di un farmacista */
    @Transactional(readOnly = true)
    public List<DispensationResponse> listByPharmacist(UUID pharmacistId) {
        return repo.findByDispensedBy(pharmacistId)
                .stream().map(DispensationResponse::from).toList();
    }

    /* 2) pazienti serviti da un farmacista */
    @Transactional(readOnly = true)
    public List<UUID> patientsByPharmacist(UUID pharmacistId) {
        return repo.findByDispensedBy(pharmacistId).stream()
                .map(Dispensation::getPrescriptionId)
                .map(prescriptions::getPrescription)          // REST call
                .filter(dto -> dto != null)
                .map(PrescriptionClient.PrescriptionDTO::patientId)
                .distinct()
                .toList();
    }

    /* 2‑bis) farmacisti che hanno servito un paziente */
    @Transactional(readOnly = true)
    public List<UUID> pharmacistsByPatient(UUID patientId) {
        List<UUID> prescIds = prescriptions.listPrescriptionIdsByPatient(patientId);
        if (prescIds == null || prescIds.isEmpty()) return List.of();

        return repo.findByPrescriptionIdIn(prescIds).stream()
                .map(Dispensation::getDispensedBy)
                .distinct()
                .toList();
    }
}
