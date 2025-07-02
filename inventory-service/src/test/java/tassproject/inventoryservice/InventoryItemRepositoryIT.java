package tassproject.inventoryservice;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tassproject.inventoryservice.repository.InventoryItemRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Testcontainers
@DataJpaTest
class InventoryItemRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("db")
                    .withUsername("user")
                    .withPassword("pwd");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    InventoryItemRepository repo;

    @Test
    void positiveStockUpdate() {
        var row = new InventoryItem();
        row.setId(UUID.randomUUID());
        row.setDrugId(UUID.randomUUID());
        row.setAvailableQuantity(10);
        row.setLastUpdated(OffsetDateTime.now());

        repo.save(row);

        var loaded = repo.findById(row.getId()).orElseThrow();
        loaded.setAvailableQuantity(loaded.getAvailableQuantity() + 5);
        repo.save(loaded);

        Assertions.assertEquals(15, repo.findById(row.getId()).orElseThrow().getAvailableQuantity());
    }
}
