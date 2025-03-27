package app.dtos;

import app.entities.Actor;
import app.entities.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@ToString
@AllArgsConstructor
@Builder
@JsonIgnoreProperties
public class ActorDTO
{
    // attributes
    @JsonProperty("id")
    private Long actorApiId;
    private String name;
    private Set<MovieDTO> movieDTOS;

    // constructors
    public ActorDTO(Actor actor)
    {
        this.actorApiId = actor.getActorApiId();
        this.name = actor.getName();
        if (actor.getMovies() != null)
        {
            Set<Movie> movieEntities = actor.getMovies();
            this.movieDTOS = new HashSet<>();
            movieEntities.forEach(movie -> this.movieDTOS.add(new MovieDTO(movie)));
        }
    }

    public ActorDTO(Actor actor, boolean includeMovies) {
        this.actorApiId = actor.getActorApiId();
        this.name = actor.getName();

        // Only load movies when explicitly requested
        if (includeMovies && actor.getMovies() != null) {
            this.movieDTOS = actor.getMovies()
                    .stream()
                    .map(movie -> new MovieDTO(movie, false)) // Prevent full actor mapping
                    .collect(Collectors.toSet());
        }
    }

    public ActorDTO(Long actorApiId, String name)
    {
        this.actorApiId = actorApiId;
        this.name = name;
    }
}
