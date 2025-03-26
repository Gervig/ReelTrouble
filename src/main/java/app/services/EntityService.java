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
    private final UserDAO userDAO;
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
        Set<Director> directors = movieDTO.getDirectors().stream()
                .map(dto ->
                {
                    Director existingDirector = directorDAO.readByApiId(dto.getDirectorApiId());

                    if (existingDirector != null)
                    {
                        existingDirector = directorDAO.update(existingDirector);
                        return existingDirector;
                    } else {
                        Director director = Director.builder()
                                .directorApiId(dto.getDirectorApiId())
                                .name(dto.getName())
                                .build();

                        director = directorDAO.create(director);
                        return director;
                    }
                })
                .collect(Collectors.toSet());

        // Hent eller opret genrer
        Set<Genre> genres = movieDTO.getGenres().stream()
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
                            .name(dto.getName())
                            .build();
                    genre = genreDAO.create(genre);
                    return genre;
                })
                .collect(Collectors.toSet());

        // Hent eller opret skuespillere
        Set<Actor> actors = movieDTO.getActors().stream()
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
                .imdbRating(movieDTO.getImdbRating())
                .directors(directors)
                .actors(actors)
                .genres(genres)
                .movieApiId(movieDTO.getMovieApiId())
                .description(movieDTO.getDescription())
                .releaseDate(movieDTO.getReleaseDate())
                .build();
        return movieDAO.create(movie);
    }

    public UserService(UserDAO userDAO)

}