package app.dtos;

import app.entities.Genre;
import app.entities.Movie;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GenreDTO
{
    private Long id;
    private String name;
    private Set<MovieDTO> movieDTOS;

    public GenreDTO(Genre genre)
    {
        this.id = id;
        this.name = name;
        if (genre.getMovie() != null)
        {
            Set<Movie> movieEntities = genre.getMovie();
            this.movieDTOS = new HashSet<>();
            movieEntities.forEach(movie -> this.movieDTOS.add(new MovieDTO(movie)));
        }
    }

    public GenreDTO(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }
}