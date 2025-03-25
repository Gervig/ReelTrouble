package app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import java.util.Set;

@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
@Entity
public class Actor
{
    // basic
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_api_id")
    private Long actorApiId;
    private String name;

        // relations
    @Setter
    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies;
}
