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

    // constructor
    public Actor(ActorDTO actorDTO)
    {
        this.actorApiId = actorDTO.getActorApiId();
        this.name = actorDTO.getName();

        if(actorDTO.getMovies() != null)
        {
            Set<MovieDTO> movieDTOS = actorDTO.getMovies();
            this.movies = new HashSet<>();
            movieDTOS.forEach(movieDTO -> this.movies.add(new Movie(movieDTO)));
        }
    }
}
