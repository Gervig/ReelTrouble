package app.dtos;

import app.entities.Genre;
import app.entities.Movie;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GenreDTO
{    // attributes
    @JsonProperty("id")
    private Long genreApiId;
    private String name;
    private Set<MovieDTO> movieDTOS;

    public GenreDTO(Genre genre)
    {
        this.genreApiId = genreApiId;
        this.name = name;
        if (genre.getMovies() != null)
        {
            Set<Movie> movieEntities = genre.getMovies();
            this.movieDTOS = new HashSet<>();
            movieEntities.forEach(movie -> this.movieDTOS.add(new MovieDTO(movie)));
        }
    }

    public GenreDTO(Long genreApiId, String name)
    {
        this.genreApiId = genreApiId;
        this.name = name;
    }
}