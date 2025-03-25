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
    private Set<MovieDTO> movies;

    // constructor
    public ActorDTO(Actor actor)
    {
        this.actorApiId = actor.getActorApiId();
        this.name = actor.getName();
        if (actor.getMovies() != null)
        {
            Set<Movie> movieEntities = actor.getMovies();
            this.movies = new HashSet<>();
            movieEntities.forEach(movie -> this.movies.add(new MovieDTO(movie)));
        }
    }
}
