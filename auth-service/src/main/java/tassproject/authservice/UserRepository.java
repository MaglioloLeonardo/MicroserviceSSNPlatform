package tassproject.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.authservice.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /* Cerca l’utente per username (e-mail normalizzata) */
    Optional<User> findByUsername(String username);
}
