package app.services;

import app.daos.UserDAO;
import app.daos.impl.ActorDAO;
import app.daos.impl.DirectorDAO;
import app.daos.impl.GenreDAO;
import app.daos.impl.MovieDAO;
import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.stream.Collectors;

public class EntityService
{
    private static EntityManagerFactory emf;

    //TODO rewrite methdod to work on a list of MovieDTO for DB optimization

    public static List<Movie> persistMovies(List<MovieDTO> movieDTOS)
    {
        MovieDAO movieDAO = MovieDAO.getInstance(emf);
        GenreDAO genreDAO = GenreDAO.getInstance(emf);
        ActorDAO actorDAO = ActorDAO.getInstance(emf);
        DirectorDAO directorDAO = DirectorDAO.getInstance(emf);

        // Get maps of existing entities
        Map<Long, Actor> actorMap = actorDAO.getActorMap();
        Map<Long, Director> directorMap = directorDAO.getDirectorMap();
        Map<Long, Genre> genreMap = genreDAO.getGenreMap();
        Map<Long, Movie> movieMap = movieDAO.getMovieMap();

        List<Actor> newActors = new ArrayList<>();
        List<Director> newDirectors = new ArrayList<>();
        List<Genre> newGenres = new ArrayList<>();
        List<Movie> newMovies = new ArrayList<>();

        for (MovieDTO movieDTO : movieDTOS)
        {
            // Process Actors
            Set<Actor> movieActors = new HashSet<>();
            for (ActorDTO actorDTO : movieDTO.getActors())
            {
                Actor actor = actorMap.get(actorDTO.getActorApiId());
                if (actor == null)
                {
                    actor = new Actor(actorDTO);
                    actorMap.put(actorDTO.getActorApiId(), actor);
                    newActors.add(actor);
                }
                movieActors.add(actor);
            }

            // Process Directors
            Set<Director> movieDirectors = new HashSet<>();
            for (DirectorDTO directorDTO : movieDTO.getDirectors())
            {
                Director director = directorMap.get(directorDTO.getDirectorApiId());
                if (director == null)
                {
                    director = new Director(directorDTO);
                    directorMap.put(directorDTO.getDirectorApiId(), director);
                    newDirectors.add(director);
                }
                movieDirectors.add(director);
            }

            // Process Genres
            Set<Genre> movieGenres = new HashSet<>();
            for (GenreDTO genreDTO : movieDTO.getGenres())
            {
                Genre genre = genreMap.get(genreDTO.getGenreApiId());
                if (genre == null)
                {
                    genre = new Genre(genreDTO);
                    genreMap.put(genreDTO.getGenreApiId(), genre);
                    newGenres.add(genre);
                }
                movieGenres.add(genre);
            }

            // Process Movies
            if (!movieMap.containsKey(movieDTO.getMovieApiId()))
            {
                Movie movie = new Movie(movieDTO);
                movie.setActors(movieActors);
                movie.setDirectors(movieDirectors);
                movie.setGenres(movieGenres);
                newMovies.add(movie);
                movieMap.put(movieDTO.getMovieApiId(), movie);
            }
        }

        // Persist all new entities
        newActors.forEach(actorDAO::create);
        newDirectors.forEach(directorDAO::create);
        newGenres.forEach(genreDAO::create);
        newMovies.forEach(movieDAO::create);

        return newMovies;
    }


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
                    } else
                    {
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
                    } else
                    {
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

}