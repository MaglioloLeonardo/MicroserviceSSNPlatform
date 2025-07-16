package tassproject.authservice;

import jakarta.persistence.*;

@Entity
@Table(name = "roles", schema = "auth_service")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    protected RoleEntity() { }

    public RoleEntity(String name) {
        this.name = name;
    }

    /* ---------- getters & setters ---------- */
    public Integer getId()            { return id; }
    public void    setId(Integer id)  { this.id = id; }

    public String  getName()          { return name; }
    public void    setName(String n)  { this.name = n; }
}
