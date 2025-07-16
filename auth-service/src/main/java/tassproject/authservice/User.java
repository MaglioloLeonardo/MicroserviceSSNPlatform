package tassproject.authservice;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", schema = "auth_service")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "session_version", nullable = false)
    private Integer sessionVersion = 0;

    /* ----------  NUOVO: ruoli M‑N  ---------- */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "auth_service",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();
    /* ---------------------------------------- */

    protected User() { }

    public User(String username, String password, Set<RoleEntity> roles) {
        this.username = username;
        this.password = password;
        this.roles    = roles;
    }

    /* ---------- getters & setters ---------- */
    public Long              getId()              { return id; }
    public void              setId(Long id)       { this.id = id; }

    public String            getUsername()        { return username; }
    public void              setUsername(String u){ this.username = u; }

    public String            getPassword()        { return password; }
    public void              setPassword(String p){ this.password = p; }

    public Integer           getSessionVersion()  { return sessionVersion; }
    public void              setSessionVersion(Integer v){ this.sessionVersion = v; }

    public Set<RoleEntity>   getRoles()           { return roles; }
    public void              setRoles(Set<RoleEntity> r){ this.roles = r; }
}
