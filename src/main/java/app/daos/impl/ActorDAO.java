package app.daos.impl;

import app.daos.IDAO;
import app.entities.Actor;
import app.entities.Actor;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;

public class ActorDAO implements IDAO<Actor, Long>
{
    private static EntityManagerFactory emf;
    private static ActorDAO instance;

    public ActorDAO()
    {
    }

    public static ActorDAO getInstance(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
            instance = new ActorDAO();
        }
        return instance;
    }

    @Override
    public Actor create(Actor actor)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(actor);
            em.getTransaction().commit();
            return actor;
        } catch (Exception e)
        {
            throw new ApiException(401, "Error creating Actor ", e);
        }
    }

    @Override
    public Actor read(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.find(Actor.class, id);
        }
    }

    @Override
    public List<Actor> readAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT d FROM Actor d", Actor.class).getResultList();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding list of Actors", e);
        }
    }

    public Actor readByApiId(Long apiID) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                return em.createQuery("SELECT d FROM Actor d WHERE d.actorApiId = :actorApiId", Actor.class)
                        .setParameter("actorApiId", apiID)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null; // Returnér null hvis ingen genre findes
            }
        }
    }


    @Override
    public Actor update(Actor actor)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Actor updatedActor = em.merge(actor);
            em.getTransaction().commit();
            return updatedActor;
        } catch (Exception e)
        {
            throw new ApiException(401, "Error updating Actor", e);
        }
    }

    @Override
    public void delete(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Actor Actor = em.find(Actor.class, id);
            if (Actor == null)
            {
                em.getTransaction().rollback();
                throw new ApiException(401, "Error deleting Actor, Actor was not found");
            }
            em.remove(Actor);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error removing Actor", e);
        }
    }
}
