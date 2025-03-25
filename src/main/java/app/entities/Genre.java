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
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "genre_name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "genre_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    @ToString.Exclude
    private Set<Movie> movie = new HashSet<>();

    public Genre(GenreDTO genreDTO)
    {
        this.id = genreDTO.getId();
        this.name = genreDTO.getName();
        if(genreDTO.getMovieDTOS()!=null){
            Set<MovieDTO> movieDTOS = genreDTO.getMovieDTOS();
            this.movie = new HashSet<>();
            movieDTOS.forEach(movieDTO -> this.movie.add(new Movie(movieDTO)));
        }
    }
}
