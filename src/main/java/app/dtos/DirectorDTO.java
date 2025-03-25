package app.dtos;

import app.entities.Director;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@JsonIgnoreProperties
public class DirectorDTO
{
    @JsonProperty("id")
    private Long directorApiId;
    private String name;

    public DirectorDTO(Director director){
        this.directorApiId = director.getDirectorApiId();
        this.name = director.getName();
    }
}
