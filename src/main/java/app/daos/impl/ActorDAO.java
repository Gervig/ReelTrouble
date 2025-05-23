package app.daos.impl;

import app.daos.IDAO;
import app.entities.Actor;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActorDAO implements IDAO<Actor, Long>
{
    private static EntityManagerFactory emf;
    private static ActorDAO instance;

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
            throw new ApiException(401, "Error creating actor", e);
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
            return em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding list of actors", e);
        }
    }

    public Actor readByApiId(Long apiID)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            try
            {
                return em.createQuery("SELECT a FROM Actor a WHERE a.actorApiId = :actorApiId", Actor.class)
                        .setParameter("actorApiId", apiID)
                        .getSingleResult();
            } catch (NoResultException e)
            {
                return null;
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
            throw new ApiException(401, "Error updating actor", e);
        }
    }

    @Override
    public void delete(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Actor actor = em.find(Actor.class, id);
            if (actor == null)
            {
                em.getTransaction().rollback();
                throw new ApiException(401, "Error deleting actor, actor was not found");
            }
            em.remove(actor);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error removing actor", e);
        }
    }

    public Map<Long, Actor> getActorMap()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<Actor> actors = em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList();
            return actors.stream().collect(Collectors.toMap(Actor::getActorApiId, Function.identity()));
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding list of actors", e);
        }
    }

    public Actor merge(Actor actor)
    {
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            actor = em.merge(actor);
            em.getTransaction().commit();
            return actor;
        } catch (Exception e)
        {
            em.getTransaction().rollback();
            throw new ApiException(500, "Error saving Actor", e);
        }
    }

}
