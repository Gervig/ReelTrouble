package app.entities;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import java.math.BigDecimal;
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
    @Column(name = "movie_api_id")
    private Long movieApiId; // the ID from TMDB's API
    @Setter
    private String title;
    @Column(columnDefinition = "TEXT") // sets the datatype to be TEXT in the database
    private String description;
    @Column(name = "imdb_rating")
    private BigDecimal imdbRating;
    @Temporal(TemporalType.DATE) // sets the datatype to be DATE in the database
    @Column(name = "release_date")
    private LocalDate releaseDate;
    private int minutes;

    // relations
    @ManyToMany(mappedBy = "likeList")
    private Set<User> users = new HashSet<>();

    //TODO test om vi kan bruge lazy fetch her
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    @ToString.Exclude
    @Setter
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
    @ToString.Exclude
    private Set<Genre> genres;

    // constructor
    public Movie(MovieDTO movieDTO)
    {
        this.movieApiId = movieDTO.getMovieApiId();
        this.title = movieDTO.getTitle();
        this.description = movieDTO.getDescription();
        this.imdbRating = movieDTO.getImdbRating();
        this.releaseDate = movieDTO.getReleaseDate();
        this.minutes = movieDTO.getMinutes();

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
