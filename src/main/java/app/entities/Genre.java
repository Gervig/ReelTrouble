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

    @Setter
    @Column(name = "genre_name")
    private String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @Setter
    @ToString.Exclude
    private Set<Movie> movies = new HashSet<>();

    // constructor
    public Genre(GenreDTO genreDTO)
    {
        this.id = genreDTO.getGenreApiId();
        this.name = genreDTO.getName();
        if(genreDTO.getMovieDTOS()!=null){
            Set<MovieDTO> movieDTOS = genreDTO.getMovieDTOS();
            this.movies = new HashSet<>();
            movieDTOS.forEach(movieDTO -> this.movies.add(new Movie(movieDTO)));
        }
    }
}
