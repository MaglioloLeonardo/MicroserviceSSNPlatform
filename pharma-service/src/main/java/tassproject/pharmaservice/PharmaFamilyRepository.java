package tassproject.pharmaservice;

import tassproject.pharmaservice.PharmaFamily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PharmaFamilyRepository extends JpaRepository<PharmaFamily, UUID> {
}