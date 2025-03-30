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
        newMovies.forEach(movieDAO::merge);

        System.out.println("Total amount of new Actors persisted: " + newActors.size());
        System.out.println("Total amount of new Directors persisted: " + newDirectors.size());
        System.out.println("Total amount of new Genres persisted: " + newGenres.size());
        System.out.println("Total amount of new Movies persisted: " + newMovies.size());

        return newMovies;
    }

}