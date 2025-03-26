package app.entities;

import app.dtos.DirectorDTO;
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
public class Director
{
    // basic attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "director_api_id")
    private Long directorApiId;

    @Setter
    private String name;

    @Setter
    @ManyToMany(mappedBy = "directors")
    private Set<Movie> movies;

    // constructor
    public Director(DirectorDTO directorDTO)
    {
        this.directorApiId = directorDTO.getDirectorApiId();
        this.name = directorDTO.getName();
        if(directorDTO.getMovieDTOS()!=null){
            Set<MovieDTO> movieDTOS = directorDTO.getMovieDTOS();
            this.movies = new HashSet<>();
            movieDTOS.forEach(movieDTO -> this.movies.add(new Movie(movieDTO)));
        }
    }
}