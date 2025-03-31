package app.dtos;

import app.entities.*;
import app.enums.MediaType;
import dk.bugelhartmann.UserDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO
{
    // attributes
    private Long id;
    private Long movieApiId;
    private String title;
    private String description;
    private BigDecimal imdbRating;
    private LocalDate releaseDate;
    private int minutes;
    private Set<UserDTO> users;
    private Set<ActorDTO> actors;
    private Set<DirectorDTO> directors;
    private Set<GenreDTO> genres;

    // constructor
    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.movieApiId = movie.getMovieApiId();
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.imdbRating = movie.getImdbRating();
        this.releaseDate = movie.getReleaseDate();
        this.minutes = movie.getMinutes();

        // Avoid infinite recursion by not creating full ActorDTO objects
        if (movie.getActors() != null) {
            this.actors = movie.getActors()
                    .stream()
                    .map(actor -> new ActorDTO(actor, false)) // Prevent full movie mapping
                    .collect(Collectors.toSet());
        }
    }

    public MovieDTO(Movie movie, boolean includeDetails) {
        this.id = movie.getId();
        this.movieApiId = movie.getMovieApiId();
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.imdbRating = movie.getImdbRating();
        this.releaseDate = movie.getReleaseDate();
        this.minutes = movie.getMinutes();

        // Only load actors if allowed
        if (includeDetails && movie.getActors() != null) {
            this.actors = movie.getActors()
                    .stream()
                    .map(actor -> new ActorDTO(actor, false))
                    .collect(Collectors.toSet());
        }

        // Only load directors if allowed
        if (includeDetails && movie.getDirectors() != null) {
            this.directors = movie.getDirectors()
                    .stream()
                    .map(director -> new DirectorDTO(director, false))
                    .collect(Collectors.toSet());
        }

        // Only load genres if allowed
        if (includeDetails && movie.getGenres() != null) {
            this.genres = movie.getGenres()
                    .stream()
                    .map(genre -> new GenreDTO(genre, false))
                    .collect(Collectors.toSet());
        }
    }



}
