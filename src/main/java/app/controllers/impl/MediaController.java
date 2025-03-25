package app.controllers.impl;

import app.controllers.IController;
import app.daos.impl.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MediaController implements IController<MovieDTO, Long>
{
    // attributes
    private static EntityManagerFactory emf;
    private MovieDAO mediaDAO;

    // constructor
    public MediaController(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
        }
        this.mediaDAO = MovieDAO.getInstance(emf);
    }

    @Override
    public List<MovieDTO> getAll()
    {
//        List<Movie> media = mediaDAO.readAll();
//
//        List<MovieDTO> mediaDTOS = media.stream()
//                .map(MovieDTO::new)
//                .toList; //
        return null;
    }

    @Override
    public MovieDTO getById(Long id)
    {
        return null;
    }
}
