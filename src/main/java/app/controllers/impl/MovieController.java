package app.controllers.impl;

import app.controllers.IController;
import app.daos.UserDAO;
import app.daos.impl.GenreDAO;
import app.daos.impl.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MovieController implements IController<MovieDTO, Long>
{
    // attributes
    private static EntityManagerFactory emf;
    private MovieDAO movieDAO;
    private GenreDAO genreDAO;
    private UserDAO userDAO;

    // constructor
    public MovieController(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
        }
        this.movieDAO = MovieDAO.getInstance(emf);
        this.genreDAO = GenreDAO.getInstance(emf);
        this.userDAO = UserDAO.getInstance(emf);
    }

    @Override
    public List<MovieDTO> getAll()
    {
        List<Movie> movies = movieDAO.readAll();

        List<MovieDTO> movieDTOS = movies.stream()
                .map(movie -> new MovieDTO(movie, true))
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
        //TODO don't overload constructors like this
        MovieDTO movieDTO = new MovieDTO(movie, true);
        return movieDTO;
    }

    public MovieDTO getRandomMovieInGenre(String genre)
    {
        Long genreID = genreDAO.findByName(genre).getId();
        List<Movie> movies = movieDAO.findMoviesByGenre(genreID);

        Movie movie = movies.get(new Random().nextInt(movies.size()));

        MovieDTO movieDTO = new MovieDTO(movie,true);
        return movieDTO;
    }

    // Random movie by userId
    public MovieDTO getRandomMovieExclUsersListWithGenre(String genre, Long userID)
    {
        List<Movie> movies = movieDAO.findMovieExclUsersListWithGenre(genre, userID);
        Movie movie = movies.get(new Random().nextInt(movies.size()));

        MovieDTO movieDTO = new MovieDTO(movie,true);
        return movieDTO;
    }

    // Random movie by username
    public MovieDTO getRandomMovieExclUsersListWithGenre(String genre, String username)
    {
        Long userID = userDAO.readByName(username).getId();
        List<Movie> movies = movieDAO.findMovieExclUsersListWithGenre(genre, userID);
        Movie movie = movies.get(new Random().nextInt(movies.size()));

        MovieDTO movieDTO = new MovieDTO(movie,true);
        return movieDTO;
    }

    // get movies on user by userId
    public List<MovieDTO> getAllMoviesOnUsersList(Long userID)
    {
        List<Movie> movies = movieDAO.findMovieInclUsersList(userID);

        List<MovieDTO> movieDTOS = movies.stream()
                .map(movie -> new MovieDTO(movie,true))
                .collect(Collectors.toList());
        return movieDTOS;
    }

    // get movies on user by username
    public List<MovieDTO> getAllMoviesOnUsersList(String username)
    {
        Long userId = userDAO.readByName(username).getId();
        List<Movie> movies = movieDAO.findMovieInclUsersList(userId);
        List<MovieDTO> movieDTOS = movies.stream()
                .map(movie -> new MovieDTO(movie,true))
                .collect(Collectors.toList());
        return movieDTOS;
    }

    //Add movie
    public MovieDTO addNewMovieToDB(MovieDTO movieDTO)
    {
        Movie movie = new Movie(movieDTO);
        movieDAO.create(movie);

        return movieDTO;
    }

    //Get all movies in a specific genre
    public List<MovieDTO> getMoviesInGenre(String genre) {
        Genre foundGenre = genreDAO.findByName(genre);
        Long genreID = foundGenre.getId();
        List<Movie> movies = movieDAO.findMoviesByGenre(genreID);

        List<MovieDTO> movieDTOS = movies.stream()
                .map(movie -> new MovieDTO(movie, true))
                .collect(Collectors.toList());
        return movieDTOS;
    }

    //Get movies that are not on the users likedList ** by userId **
    public MovieDTO getRandomMovieExclUsersList(Long userID)
    {
        List<Movie> movies = movieDAO.findMoviesExclUsersList(userID);
        Movie movie = movies.get(new Random().nextInt(movies.size()));
        MovieDTO movieDTO = new MovieDTO(movie, true);
        return movieDTO;
    }

    //Get movies that are not on the users likedList ** by username **
    public MovieDTO getRandomMovieExclUsersList(String username)
    {
        Long userID = userDAO.readByName(username).getId();
        List<Movie> movies = movieDAO.findMoviesExclUsersList(userID);
        Movie movie = movies.get(new Random().nextInt(movies.size()));
        MovieDTO movieDTO = new MovieDTO(movie, true);
        return movieDTO;
    }

}
