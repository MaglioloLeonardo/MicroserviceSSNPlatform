package tassproject.prescriptionservice;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tassproject.prescriptionservice.repository.PrescriptionItemRepository;
import tassproject.prescriptionservice.repository.PrescriptionRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PrescriptionApplicationService {

    private final PrescriptionRepository repo;
    private final PrescriptionItemRepository items;
    private final RabbitTemplate rabbitTemplate;

    public PrescriptionApplicationService(PrescriptionRepository repo,
                                          PrescriptionItemRepository items,
                                          RabbitTemplate rabbitTemplate) {
        this.repo           = repo;
        this.items          = items;
        this.rabbitTemplate = rabbitTemplate;
    }

    /* ------------------------------ CREA ------------------------------ */
    public PrescriptionResponse create(CreatePrescriptionRequest req) {
        List<PrescriptionItem> itemEntities = req.items().stream()
                .map(dto -> new PrescriptionItem(
                        dto.drugId(),
                        dto.activeIngredientId(),
                        dto.activeIngredient(),
                        dto.dosage(),
                        dto.quantity()))
                .toList();

        Prescription entity = Prescription.create(
                req.doctorId(),
                req.patientId(),
                req.exemption(),
                req.therapyDuration(),
                itemEntities);

        entity = repo.save(entity);
        rabbitTemplate.convertAndSend("prescription.events", "PrescriptionCreated", entity.getId());
        return PrescriptionResponse.from(entity);
    }

    /* ------------------------------ QUERY BASE ------------------------ */
    @Transactional(readOnly = true)
    public PrescriptionResponse findById(UUID id) {
        return repo.findById(id).map(PrescriptionResponse::from).orElseThrow();
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

    /* ------------------------------ UPDATE STATO ---------------------- */
    public PrescriptionResponse updateStatus(UUID id, Prescription.Status newStatus) {
        Prescription entity = repo.findById(id).orElseThrow();
        switch (newStatus) {
            case DISPENSED  -> entity.markDispensed();
            case COMPLETED  -> entity.markCompleted();
            case CANCELLED  -> entity.cancel();
            default -> throw new IllegalStateException("Unsupported status transition");
        }
        repo.save(entity);
        rabbitTemplate.convertAndSend("prescription.events","PrescriptionStatusChanged", entity.getId());
        return PrescriptionResponse.from(entity);
    }

    /* ------------------------------ NUOVE QUERY ----------------------- */

    /* 1) Pazienti di un medico */
    @Transactional(readOnly = true)
    public List<UUID> getPatientsByDoctor(UUID doctorId) {
        return repo.findDistinctPatientIdsByDoctorId(doctorId);
    }

    /* 1‑bis) Medici di un paziente */
    @Transactional(readOnly = true)
    public List<UUID> getDoctorsByPatient(UUID patientId) {
        return repo.findDistinctDoctorIdsByPatientId(patientId);
    }

    /* 3) Tutte le prescrizioni di un medico */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> findByDoctor(UUID doctorId) {
        return repo.findByDoctorId(doctorId).stream()
                .map(PrescriptionResponse::from)
                .toList();
    }

    /* 6) Farmaci acquistati (erogati) da un paziente */
    @Transactional(readOnly = true)
    public List<UUID> getDrugIdsByPatient(UUID patientId) {
        return items.findDistinctDrugIdsByPatientId(patientId);
    }
}
