package app.entities;

import app.enums.MediaType;
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
public class Media
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
    @Column(name = "media_type")
    private MediaType mediaType;
    @Column(name = "imdb_url")
    private String imdbUrl;
    @Column(name = "imdb_rating")
    private BigDecimal imdbRating;
    @Temporal(TemporalType.DATE) // sets the datatype to be DATE in the database
    @Column(name = "release_date")
    private LocalDate releaseDate;
    private Time duration;
    @Column(nullable = true)
    private int episodes;
    @Column(nullable = true)
    private int seasons;

        // relations
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "media_users",
            joinColumns = @JoinColumn(name = "media_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "media", fetch = FetchType.LAZY)
    private Set<Genre> genres = new HashSet<>();

}
