package app.daos.impl;

import app.daos.IDAO;
import app.entities.Media;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MediaDAO implements IDAO<Media, Long>
{
    // attributes
    private static EntityManagerFactory emf;
    private static MediaDAO instance;

    // singleton **
    public MediaDAO(){}

    public static MediaDAO getInstance(EntityManagerFactory _emf)
    {
        if (instance == null)
        {
            instance = new MediaDAO();
            emf = _emf;
        }
        return instance;
    }
    @Override
    public Media create(Media type)
    {
        return null;
    }

    @Override
    public Media read(Long aLong)
    {
        return null;
    }

    @Override
    public List<Media> readAll()
    {
        return null;
    }

    @Override
    public Media update(Media type)
    {
        return null;
    }

    @Override
    public void delete(Long aLong)
    {

    }
}
