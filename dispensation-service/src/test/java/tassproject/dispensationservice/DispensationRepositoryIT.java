package tassproject.dispensationservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

@Testcontainers
@DataJpaTest
class DispensationRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("db")
                    .withUsername("user")
                    .withPassword("pwd");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private tassproject.dispensationservice.repository.DispensationRepository repo;

    @Test
    void saveAndQuery() {
        var disp = new Dispensation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                UUID.randomUUID(),
                Dispensation.Status.DONE
        );
        repo.save(disp);

        Assertions.assertEquals(1, repo.findAll().size());
        Assertions.assertTrue(repo.existsById(disp.getId()));
    }
}
