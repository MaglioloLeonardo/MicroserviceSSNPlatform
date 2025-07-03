package tassproject.prescriptionservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tassproject.prescriptionservice.CreatePrescriptionRequest;
import tassproject.prescriptionservice.PrescriptionResponse;
import tassproject.prescriptionservice.Prescription;
import tassproject.prescriptionservice.PrescriptionItem;
import tassproject.prescriptionservice.repository.PrescriptionRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PrescriptionApplicationService {
    private final PrescriptionRepository repo;
    private final RabbitTemplate rabbitTemplate;

    // ← il costruttore esplicito che inizializza i final fields
    public PrescriptionApplicationService(PrescriptionRepository repo,
                                          RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    public PrescriptionResponse create(CreatePrescriptionRequest req) {
        var items = req.items().stream()
                .map(dto -> new PrescriptionItem(
                        dto.drugId(),
                        dto.activeIngredient(),
                        dto.dosage(),
                        dto.quantity()))
                .toList();

        var entity = Prescription.create(
                req.doctorId(),
                req.patientId(),
                req.exemption(),
                req.therapyDuration(),
                items);

        entity = repo.save(entity);
        rabbitTemplate.convertAndSend(
                "prescription.events",
                "PrescriptionCreated",
                entity.getId());

        return PrescriptionResponse.from(entity);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse findById(UUID id) {
        return repo.findById(id)
                .map(PrescriptionResponse::from)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> findActiveByPatientId(UUID patientId) {
        return repo.findAll().stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .filter(p -> p.getStatus() == Prescription.Status.OPEN
                        || p.getStatus() == Prescription.Status.DISPENSED)
                .map(PrescriptionResponse::from)
                .toList();
    }

    public PrescriptionResponse updateStatus(UUID id, Prescription.Status newStatus) {
        var entity = repo.findById(id).orElseThrow();
        switch (newStatus) {
            case DISPENSED   -> entity.markDispensed();
            case COMPLETED   -> entity.markCompleted();
            case CANCELLED   -> entity.cancel();
            default          -> throw new IllegalStateException(
                    "Unsupported status transition");
        }
        repo.save(entity);
        rabbitTemplate.convertAndSend(
                "prescription.events",
                "PrescriptionStatusChanged",
                entity.getId());

        return PrescriptionResponse.from(entity);
    }
}
