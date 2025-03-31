package app.dtos;

import app.entities.Genre;
import app.entities.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    @JsonIgnore
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

    public GenreDTO(Genre genre, boolean includeMovies) {
        this.genreApiId = genre.getGenreApiId();
        this.name = genre.getName();

        // Only include movies if explicitly allowed
        if (includeMovies && genre.getMovies() != null) {
            this.movieDTOS = genre.getMovies()
                    .stream()
                    .map(movie -> new MovieDTO(movie, false)) // Prevent full genre mapping
                    .collect(Collectors.toSet());
        }
    }


    public GenreDTO(Long genreApiId, String name)
    {
        this.genreApiId = genreApiId;
        this.name = name;
    }
}