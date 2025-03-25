package app.services;

import app.daos.impl.ActorDAO;
import app.daos.impl.DirectorDAO;
import app.daos.impl.GenreDAO;
import app.daos.impl.MovieDAO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

public class EntityService
{
    private static EntityManagerFactory emf;

    @Transactional
    public static Movie persistMovie(MovieDTO movieDTO)
    {
        MovieDAO movieDAO = MovieDAO.getInstance(emf);
        GenreDAO genreDAO = GenreDAO.getInstance(emf);
        ActorDAO actorDAO = ActorDAO.getInstance(emf);
        DirectorDAO directorDAO = DirectorDAO.getInstance(emf);

        //TODO: Indtil videre tjekker den ikke om filmen allerede eksisterer

        // Hent eller opret director
        Director director = null;
        if (movieDTO.getDirectors() != null)
        {
            Director existingDirector = directorDAO.readByApiId(movieDTO.getDirectors().getDirectorApiId());
            if (existingDirector != null)
            {
                existingDirector = directorDAO.update(existingDirector);
                director = existingDirector;
            } else if (movieDTO.getDirectors() != null && existingDirector == null)
            {
                director = Director.builder()
                        .directorApiId(movieDTO.getDirectors().getDirectorApiId())
                        .name(movieDTO.getDirectors().getName())
                        .build();
                director = directorDAO.create(director);
            }
        }

        // Hent eller opret genrer
        Set<Genre> genres = movieDTO.getGenreDTOs().stream()
                .map(dto ->
                {
                    Genre exsistningGenre = genreDAO.readByApiId(dto.getGenreApiId());
                    if (exsistningGenre != null)
                    {
                        exsistningGenre = genreDAO.update(exsistningGenre);
                        return exsistningGenre;
                    }

                    Genre genre = Genre.builder()
                            .genreApiId(dto.getGenreApiId())
                            .name(dto.getGenre())
                            .build();
                    genre = genreDAO.create(genre);
                    return genre;
                })
                .collect(Collectors.toSet());

        // Hent eller opret skuespillere
        Set<Actor> actors = movieDTO.getActorDTOS().stream()
                .map(dto ->
                {
                    Actor existingActor = actorDAO.readByApiId(dto.getActorApiId());

                    if (existingActor != null)
                    {
                        existingActor = actorDAO.update(existingActor);
                        return existingActor;
                    } else {
                        Actor actor = Actor.builder()
                                .actorApiId(dto.getActorApiId())
                                .name(dto.getName())
                                .build();

                        actor = actorDAO.create(actor);
                        return actor;
                    }
                })
                .collect(Collectors.toSet());

        // Opret eller opdater film
        Movie movie = Movie.builder()
                .title(movieDTO.getTitle())
                .rating(movieDTO.getRating())
                .director(director)
                .actors(actors)
                .genres(genres)
                .movieApiId(movieDTO.getMovieApiID())
                .description(movieDTO.getDescription())
                .releaseDate(movieDTO.getReleaseDate())

                .build();
        return movieDAO.create(movie);
    }
}