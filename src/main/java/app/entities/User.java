package app.entities;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@DynamicUpdate
@NamedQueries(@NamedQuery(name = "Users.deleteAllRows", query = "DELETE from User"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "users") // the name user is reserved in postgres
public class User
{
    // basic attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Basic(optional = false)
    @Column(length = 25)
    @Setter
    private String name;
    @Setter
    @Basic(optional = false)
    private String password;
    @Setter
    private LocalDateTime created;

    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id",
                    referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_name",
                    referencedColumnName = "name")})
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @Setter
    private Set<Role> roles = new HashSet<>();

    // relations
    @ManyToMany(mappedBy = "users")
    private Set<Movie> likeList = new HashSet<>();

    // constructor
    public User(String username, String password)
    {
        this.name = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt()); // hashes the password
    }

    public Set<String> getRolesAsStrings()
    {
        if (roles.isEmpty())
        {
            return null;
        }
        Set<String> rolesAsStrings = new HashSet<>();
        roles.forEach((role) ->
        {
            rolesAsStrings.add(role.getRoleName());
        });
        return rolesAsStrings;
    }

}
