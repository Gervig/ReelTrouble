package app.daos.impl;

import app.daos.IDAO;
import app.entities.Director;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;

public class DirectorDAO implements IDAO<Director, Long>
{
    private static EntityManagerFactory emf;
    private static DirectorDAO instance;

    public static DirectorDAO getInstance(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
            instance = new DirectorDAO();
        }
        return instance;
    }

    @Override
    public Director create(Director director)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(director);
            em.getTransaction().commit();
            return director;
        } catch (Exception e)
        {
            throw new ApiException(401, "Error creating director ", e);
        }
    }

    @Override
    public Director read(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.find(Director.class, id);
        }
    }

    @Override
    public List<Director> readAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT d FROM Director d", Director.class).getResultList();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding list of directors", e);
        }
    }

    public Director readByApiId(Long apiID) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                return em.createQuery("SELECT d FROM Director d WHERE d.directorApiId = :directorApiId", Director.class)
                        .setParameter("directorApiId", apiID)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null; // Returnér null hvis ingen genre findes
            }
        }
    }


    @Override
    public Director update(Director director)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Director updatedDirector = em.merge(director);
            em.getTransaction().commit();
            return updatedDirector;
        } catch (Exception e)
        {
            throw new ApiException(401, "Error updating director", e);
        }
    }

    @Override
    public void delete(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Director director = em.find(Director.class, id);
            if (director == null)
            {
                em.getTransaction().rollback();
                throw new ApiException(401, "Error deleting director, director was not found");
            }
            em.remove(director);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error removing director", e);
        }
    }
}