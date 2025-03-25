package app.daos.impl;

import app.daos.IDAO;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO implements IDAO<Movie, Long>
{
    // attributes
    private static EntityManagerFactory emf;
    private static MovieDAO instance;

    // singleton **
    public MovieDAO(){}

    public static MovieDAO getInstance(EntityManagerFactory _emf)
    {
        if (instance == null)
        {
            instance = new MovieDAO();
            emf = _emf;
        }
        return instance;
    }
    @Override
    public Movie create(Movie type)
    {
        return null;
    }

    @Override
    public Movie read(Long aLong)
    {
        return null;
    }

    @Override
    public List<Movie> readAll()
    {
        return null;
    }

    @Override
    public Movie update(Movie type)
    {
        return null;
    }

    @Override
    public void delete(Long aLong)
    {

    }
}
