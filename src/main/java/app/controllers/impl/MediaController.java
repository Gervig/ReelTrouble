package app.controllers.impl;

import app.controllers.IController;
import app.daos.impl.MediaDAO;
import app.dtos.MediaDTO;
import app.entities.Media;
import app.exceptions.ApiException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MediaController implements IController<MediaDTO, Long>
{
    // attributes
    private static EntityManagerFactory emf;
    private MediaDAO mediaDAO;

    // constructor
    public MediaController(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
        }
        this.mediaDAO = MediaDAO.getInstance(emf);
    }

    @Override
    public List<MediaDTO> getAll()
    {
        List<Media> media = mediaDAO.readAll();

        List<MediaDTO> mediaDTOS = media.stream()
                .map(MediaDTO::new)
                .toList; //TODO
    }

    @Override
    public MediaDTO getById(Long id)
    {
        return null;
    }
}
