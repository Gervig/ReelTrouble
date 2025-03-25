package app.dtos;

import app.entities.Movie;
import app.entities.User;
import app.enums.MediaType;
import dk.bugelhartmann.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Long id;
    private Long mediaApiId;
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

    public MovieDTO(Movie media)
    {
        this.id = media.getId();
        this.mediaApiId = media.getMediaApiID();
        this.title = media.getTitle();
        this.description = media.getTitle();
        this.mediaType = media.getMediaType();
        this.imdbUrl = media.getImdbUrl();
        this.imdbRating = media.getImdbRating();
        this.releaseDate = media.getReleaseDate();
        this.duration = media.getDuration();
        this.episodes = media.getEpisodes();
        this.seasons = media.getSeasons();
        if(media.getUsers() != null)
        {
            Set<User> userEntities = media.getUsers();
            this.users = new HashSet<>();

            userEntities.forEach(user -> this.users.add(
                            UserDTO.builder()
                                    .username(user.getName())
                                    .password(user.getPassword())
                                    .roles(user.getRolesAsStrings())
                                    .build()
            ));
        }
    }
}
