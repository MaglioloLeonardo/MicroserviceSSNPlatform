package tassproject.dispensationservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.dispensationservice.repository.DispensationRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DispensationApplicationService {

    private final DispensationRepository repo;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public DispensationApplicationService(DispensationRepository repo,
                                          RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DispensationResponse dispense(CreateDispensationRequest request) {
        var entity = new Dispensation(
                UUID.randomUUID(),
                request.prescriptionId(),
                OffsetDateTime.now(),
                request.dispensedBy(),
                Dispensation.Status.DONE
        );
        entity = repo.save(entity);
        rabbitTemplate.convertAndSend(
                "dispensation.events",
                "PrescriptionDispensed",
                entity.getPrescriptionId()
        );
        return DispensationResponse.from(entity);
    }

    @Transactional(readOnly = true)
    public List<DispensationResponse> listByPrescription(UUID prescriptionId) {
        return repo.findAll().stream()
                .filter(d -> d.getPrescriptionId().equals(prescriptionId))
                .map(DispensationResponse::from)
                .toList();
    }
}
