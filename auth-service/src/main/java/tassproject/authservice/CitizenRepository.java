package tassproject.authservice;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.authservice.Citizen;

import java.util.UUID;

public interface CitizenRepository extends JpaRepository<Citizen, UUID> {
    boolean existsByCf(String cf);
}