package app.dtos;

import app.entities.Director;
import app.entities.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class DirectorDTO
{
    // attributes
    @JsonProperty("id")
    private Long directorApiId;
    private String name;
    @JsonIgnore
    private Set<MovieDTO> movieDTOS;

    // constructors
    public DirectorDTO(Director director){
        this.directorApiId = director.getDirectorApiId();
        this.name = director.getName();

        if(director.getMovies()!=null){
            Set<Movie> movieEntities = director.getMovies();
            this.movieDTOS = new HashSet<>();
            movieEntities.forEach(movie -> this.movieDTOS.add(new MovieDTO(movie)));
        }
    }

    public DirectorDTO(Director director, boolean includeMovies) {
        this.directorApiId = director.getDirectorApiId();
        this.name = director.getName();

        // Only include movies if explicitly allowed
        if (includeMovies && director.getMovies() != null) {
            this.movieDTOS = director.getMovies()
                    .stream()
                    .map(movie -> new MovieDTO(movie, false)) // Prevent full director mapping
                    .collect(Collectors.toSet());
        }
    }


    public DirectorDTO(Long directorApiId, String name)
    {
        this.directorApiId = directorApiId;
        this.name = name;
    }
}
