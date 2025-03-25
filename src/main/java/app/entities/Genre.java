package app.entities;

import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import java.util.HashSet;
import java.util.Set;

@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
@Entity
public class Genre
{
    // basic attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "genre_api_id")
    private Long genreApiId;

    @Column(name = "genre_name")
    private String name;

        // relations
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "genre_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    @ToString.Exclude
    private Set<Movie> movie = new HashSet<>();

    // constructor
    public Genre(GenreDTO genreDTO)
    {
        this.id = genreDTO.getGenreApiId();
        this.name = genreDTO.getName();
        if(genreDTO.getMovieDTOS()!=null){
            Set<MovieDTO> movieDTOS = genreDTO.getMovieDTOS();
            this.movie = new HashSet<>();
            movieDTOS.forEach(movieDTO -> this.movie.add(new Movie(movieDTO)));
        }
    }
}
