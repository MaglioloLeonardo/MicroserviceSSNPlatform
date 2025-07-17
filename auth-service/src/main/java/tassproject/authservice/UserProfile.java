package tassproject.authservice;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_profiles", schema = "auth_service")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "citizen_id", nullable = false, columnDefinition = "uuid", unique = true)
    private UUID citizenId;

    protected UserProfile() {}

    public UserProfile(Long userId, UUID citizenId) {
        this.userId    = userId;
        this.citizenId = citizenId;
    }

    /* getters */
    public Long getUserId()   { return userId;   }
    public UUID getCitizenId(){ return citizenId;}
}