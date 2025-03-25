package app.entities;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
@Entity
public class Movie
{
    // basic attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "media_api_id")
    private Long mediaApiID; // the ID from TMDB's API
    private String title;
    @Column(columnDefinition = "TEXT") // sets the datatype to be TEXT in the database
    private String description;
    @Column(name = "imdb_url")
    private String imdbUrl;
    @Column(name = "imdb_rating")
    private BigDecimal imdbRating;
    @Temporal(TemporalType.DATE) // sets the datatype to be DATE in the database
    @Column(name = "release_date")
    private LocalDate releaseDate;
    private Time duration;

    // relations
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_users",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    @ToString.Exclude
    private Set<Actor> actors = new HashSet<>();

    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_director",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    @ToString.Exclude
    private Set<Director> directors = new HashSet<>();

    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    // constructor
    public Movie(MovieDTO movieDTO)
    {
        this.mediaApiID = movieDTO.getMovieApiId();
        this.title = movieDTO.getTitle();
        this.description = movieDTO.getDescription();
        this.imdbUrl = movieDTO.getImdbUrl();
        this.imdbRating = movieDTO.getImdbRating();
        this.releaseDate = movieDTO.getReleaseDate();
        this.duration = movieDTO.getDuration();

        if(movieDTO.getActors() != null)
        {
            Set<ActorDTO> actorDTOS = movieDTO.getActors();
            this.actors = new HashSet<>();
            actorDTOS.forEach(actorDTO -> this.actors.add(new Actor(actorDTO)));
        }

        if(movieDTO.getDirectors() != null)
        {
            Set<DirectorDTO> directorDTOS = movieDTO.getDirectors();
            this.directors = new HashSet<>();
            directorDTOS.forEach(directorDTO -> this.directors.add(new Director(directorDTO)));
        }

        if(movieDTO.getGenres() != null)
        {
            Set<GenreDTO> genreDTOS = movieDTO.getGenres();
            this.genres = new HashSet<>();
            genreDTOS.forEach(genreDTO -> this.genres.add(new Genre(genreDTO)));
        }

    }
}
