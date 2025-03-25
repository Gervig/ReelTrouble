package app.dtos;

import app.entities.Actor;
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
public class ActorDTO
{
    @JsonProperty("id")
    private Long actorApiId;
    private String name;

    public ActorDTO(Actor actor)
    {
        this.actorApiId = actor.getActorApiId();
        this.name = actor.getName();
    }
}
