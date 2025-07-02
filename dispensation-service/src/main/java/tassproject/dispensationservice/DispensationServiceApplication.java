package tassproject.dispensationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        // Se hai anche componenti (ad es. @Service, @Component) in prescription-service
        scanBasePackages = {
                "tassproject.dispensationservice",
                "tassproject.prescriptionservice"
        }
)
@EntityScan(basePackages = {
        "tassproject.dispensationservice",     // le tue entità locali
        "tassproject.prescriptionservice"      // quelle di Prescription
})
@EnableJpaRepositories(basePackages = {
        "tassproject.dispensationservice.repository",   // i repo di Dispensation
        "tassproject.prescriptionservice.repository"   // i repo di Prescription
})
public class DispensationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DispensationServiceApplication.class, args);
    }

}
