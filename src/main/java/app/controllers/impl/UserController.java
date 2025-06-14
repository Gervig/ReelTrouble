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

    // add to like-list by userId
    public MovieDTO postMovieToUsersList(Long movieID, Long userID)
    {
        Movie movie = movieDAO.read(movieID);
        userDAO.addMovieToList(userID, movieID);
        MovieDTO movieDTO = new MovieDTO(movie);
        return movieDTO;
    }

    // add to like-list by username
    public MovieDTO postMovieToUsersList(Long movieID, String username)
    {
        Movie movie = movieDAO.read(movieID);
        Long userID = userDAO.readByName(username).getId();
        userDAO.addMovieToList(userID, movieID);
        MovieDTO movieDTO = new MovieDTO(movie, true);
        return movieDTO;
    }

    // remove from like-list by username
    public MovieDTO deleteMovieFromUsersList(Long movieID, String username)
    {
        Movie movie = movieDAO.read(movieID);
        Long userID = userDAO.readByName(username).getId();
        userDAO.deleteMovieFromList(userID, movieID);
        MovieDTO movieDTO = new MovieDTO(movie, true);
        return movieDTO;
    }
}
