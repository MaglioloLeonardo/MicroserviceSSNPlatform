package tassproject.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tassproject.authservice.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}