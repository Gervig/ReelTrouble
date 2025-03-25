package app.dtos;

import app.entities.Director;
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
public class DirectorDTO
{
    @JsonProperty("id")
    private Long directorApiId;
    private String name;
    private Set<MovieDTO> movieDTOS;

    public DirectorDTO(Director director){
        this.directorApiId = director.getDirectorApiId();
        this.name = director.getName();

        if(director.getMovies()!=null){
            Set<Movie> movieEntities = director.getMovies();
            this.movieDTOS = new HashSet<>();
            movieEntities.forEach(movie -> this.movieDTOS.add(new MovieDTO(movie)));
        }
    }
}
