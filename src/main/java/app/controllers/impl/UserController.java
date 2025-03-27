package app.controllers.impl;

import app.daos.UserDAO;
import app.daos.impl.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Movie;
import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

public class UserController
{
    // attributes
    private static EntityManagerFactory emf;
    private UserDAO userDAO;
    private MovieDAO movieDAO;

    // constructor
    public UserController(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
        }
        this.userDAO = UserDAO.getInstance(emf);
        this.movieDAO = MovieDAO.getInstance(emf);
    }

    public MovieDTO postMovieToUsersList(Long movieID, Long userID)
    {
        Movie movie = movieDAO.read(movieID);
        userDAO.addMovieToList(userID, movieID);
        MovieDTO movieDTO = new MovieDTO(movie);
        return movieDTO;
    }
}
