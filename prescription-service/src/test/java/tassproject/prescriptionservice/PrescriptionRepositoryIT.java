package tassproject.prescriptionservice;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tassproject.prescriptionservice.repository.PrescriptionRepository;
import tassproject.prescriptionservice.repository.PrescriptionItemRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Testcontainers
@DataJpaTest
class PrescriptionRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("db")
                    .withUsername("user")
                    .withPassword("pwd");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    PrescriptionRepository prescriptions;

    @Autowired
    PrescriptionItemRepository items;

    @Test
    void crudEndToEnd() {
        // --- create ---
        var item  = new PrescriptionItem(UUID.randomUUID(), "Paracetamolo", "500mg", 2);
        var presc = Prescription.create(UUID.randomUUID(), UUID.randomUUID(), false,
                "7 giorni", List.of(item));
        presc = prescriptions.save(presc);

        Assertions.assertNotNull(presc.getId());
        Assertions.assertEquals(1, presc.getItems().size());

        // --- read ---
        var found = prescriptions.findById(presc.getId()).orElseThrow();
        Assertions.assertEquals(presc.getDoctorId(), found.getDoctorId());

        // --- update ---
        found.markDispensed();
        prescriptions.save(found);
        Assertions.assertTrue(prescriptions.findById(found.getId()).orElseThrow().isDispensed());

        // --- delete (cascata su items) ---
        prescriptions.delete(found);
        Assertions.assertTrue(prescriptions.findAll().isEmpty());
        Assertions.assertTrue(items.findAll().isEmpty());
    }
}
