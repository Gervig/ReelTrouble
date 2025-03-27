package app.controllers.impl;

import app.controllers.IController;
import app.daos.impl.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MovieController implements IController<MovieDTO, Long>
{
    // attributes
    private static EntityManagerFactory emf;
    private MovieDAO movieDAO;

    // constructor
    public MovieController(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
        }
        this.movieDAO = MovieDAO.getInstance(emf);
    }

    @Override
    public List<MovieDTO> getAll()
    {
        List<Movie> movie = movieDAO.readAll();
        List<MovieDTO> movieDTOS = movie.stream()
                .map(MovieDTO::new)
                .toList();
        return movieDTOS;
    }

    @Override
    public MovieDTO getById(Long id)
    {
        Movie movie = movieDAO.read(id);
        if (movie == null)
        {
            return null;
        }
        MovieDTO movieDTO = new MovieDTO(movie);
        return movieDTO;
    }

    public MovieDTO getRandomMovieInGenre(String genre)
    {
        List<Movie> movies = movieDAO.findMoviesByGenre(genre);

        Movie movie = movies.get(new Random().nextInt(movies.size()));

        MovieDTO movieDTO = new MovieDTO(movie);
        return movieDTO;
    }

    public MovieDTO getRandomMovieExclUsersList(String genre, Long userID)
    {
        List<Movie> movies = movieDAO.findMovieExclUsersList(genre, userID);
        Movie movie = movies.get(new Random().nextInt(movies.size()));

        MovieDTO movieDTO = new MovieDTO(movie);
        return movieDTO;
    }

    public List<MovieDTO> getAllMoviesOnUsersList(Long userID)
    {
        List<Movie> movies = movieDAO.findMovieInclUsersList(userID);

        List<MovieDTO> movieDTOS = movies.stream()
                .map(movie -> new MovieDTO(movie))
                .collect(Collectors.toList());
        return movieDTOS;
    }

    //Add movie
    public MovieDTO addNewMovieToDB(MovieDTO movieDTO){

        Movie movie = new Movie(movieDTO);
        movieDAO.create(movie);

        return movieDTO;
    }
}
