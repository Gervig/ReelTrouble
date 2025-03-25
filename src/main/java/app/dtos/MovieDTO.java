package app.dtos;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.entities.User;
import app.enums.MediaType;
import dk.bugelhartmann.UserDTO;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private MediaType mediaType;
    private String imdbUrl;
    private BigDecimal imdbRating;
    private LocalDate releaseDate;
    private Time duration;
    private int episodes;
    private int seasons;
    private Set<UserDTO> users;
    private Set<ActorDTO> actors;
    private Set<DirectorDTO> directors;

    // constructor
    public MovieDTO(Movie movie)
    {
        this.id = movie.getId();
        this.movieApiId = movie.getMediaApiID();
        this.title = movie.getTitle();
        this.description = movie.getTitle();
        this.imdbUrl = movie.getImdbUrl();
        this.imdbRating = movie.getImdbRating();
        this.releaseDate = movie.getReleaseDate();
        this.duration = movie.getDuration();
        if(movie.getUsers() != null)
        {
            Set<User> userEntities = movie.getUsers();
            this.users = new HashSet<>();

            userEntities.forEach(user -> this.users.add(
                            UserDTO.builder()
                                    .username(user.getName())
                                    .password(user.getPassword())
                                    .roles(user.getRolesAsStrings())
                                    .build()
            ));
        }
        if(movie.getActors() != null)
        {
            Set<Actor> actorEntities = movie.getActors();
            this.actors = new HashSet<>();
            actorEntities.forEach(actor -> this.actors.add(new ActorDTO(actor)));
        }

        if(movie.getDirectors() != null){
            Set<Director> directorEntities = movie.getDirectors();
            this.directors = new HashSet<>();
            directorEntities.forEach(director -> this.directors.add(new DirectorDTO(director)));
        }
    }
}
