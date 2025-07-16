package tassproject.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.authservice.RoleEntity;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
}
