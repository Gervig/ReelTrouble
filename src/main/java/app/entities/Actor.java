package app.entities;

import app.dtos.ActorDTO;
import app.dtos.MovieDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
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
    // basic attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_api_id")
    private Long actorApiId;
    @Setter
    private String name;

        // relations
    @Setter
    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Movie> movies;

    // constructor
    public Actor(ActorDTO actorDTO)
    {
        this.actorApiId = actorDTO.getActorApiId();
        this.name = actorDTO.getName();

        if(actorDTO.getMovieDTOS() != null)
        {
            Set<MovieDTO> movieDTOS = actorDTO.getMovieDTOS();
            this.movies = new HashSet<>();
            movieDTOS.forEach(movieDTO -> this.movies.add(new Movie(movieDTO)));
        }
    }
}
