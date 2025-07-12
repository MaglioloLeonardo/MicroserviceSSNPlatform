package tassproject.authservice;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /* ⇢ NUOVO ⇠  --------------------------------------------- */
    /** Versione della sessione.
     *  Ogni logout incrementa questo valore; i JWT precedenti
     *  (che portano una 'ver' più bassa) diventano invalidi. */
    @Column(name = "session_version", nullable = false)
    private Integer sessionVersion = 0;
    /* -------------------------------------------------------- */

    /* --- costruttori --------------------------------------- */
    protected User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role     = role;
        this.sessionVersion = 0;
    }

    /* --- getters & setters -------------------------------- */
    public Long getId()                     { return id; }
    public String getUsername()             { return username; }
    public String getPassword()             { return password; }
    public Role getRole()                   { return role; }
    public Integer getSessionVersion()      { return sessionVersion; }

    public void setId(Long id)                  { this.id = id; }
    public void setUsername(String username)    { this.username = username; }
    public void setPassword(String password)    { this.password = password; }
    public void setRole(Role role)              { this.role = role; }
    public void setSessionVersion(Integer ver)  { this.sessionVersion = ver; }
}
