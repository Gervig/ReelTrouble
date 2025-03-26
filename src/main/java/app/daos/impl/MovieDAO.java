package app.daos.impl;

import app.daos.IDAO;
import app.entities.Actor;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieDAO implements IDAO<Movie, Long>
{
    // attributes

    private static EntityManagerFactory emf;
    private static MovieDAO instance;

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
    public Movie create(Movie movie)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            try
            {
                em.getTransaction().begin();



                // Sikrer unikke skuespillere ved at hente dem fra databasen, hvis de findes
                Set<Actor> uniqueActors = new HashSet<>();
                for (Actor actor : movie.getActors())
                {
                    Actor existingActor = em.createQuery("SELECT a FROM Actor a WHERE a.actorApiId = :apiId", Actor.class)
                            .setParameter("apiId", actor.getActorApiId())
                            .getResultStream()
                            .findFirst()
                            .orElse(actor); // Hvis skuespilleren ikke findes, brug den nye

                    uniqueActors.add(existingActor);
                }

                // Opdater filmens skuespiller-liste, så den kun har unikke skuespillere
                movie.setActors(uniqueActors);

                em.persist(movie);
                em.getTransaction().commit();
                return movie;
            } catch (Exception e)
            {
                em.getTransaction().rollback();
                throw new ApiException(401, "Error creating movie " + movie.getMovieApiID(), e);
            }
        }
    }

    @Override
    public Movie read(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.find(Movie.class, id);
        }
    }

    @Override
    public List<Movie> readAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT m FROM Movie m ORDER BY m.id", Movie.class).getResultList();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding list of movies", e);
        }
    }

    public List<Movie> readWithDetailsByTitle(String title) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "LEFT JOIN FETCH m.actors " +
                                    "LEFT JOIN FETCH m.genres " +
                                    "LEFT JOIN FETCH m.directors " +
                                    "WHERE LOWER(m.title) LIKE LOWER(:title)", Movie.class)
                    .setParameter("title", "%" + title + "%") // Wildcards før og efter
                    .getResultList();
        }
    }


    public Movie readWithDetails(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "LEFT JOIN FETCH m.actors " +
                                    "LEFT JOIN FETCH m.genres " +
                                    "WHERE m.id = :id", Movie.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    @Override
    public Movie update(Movie movie)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Movie updatedMovie = em.merge(movie);
            em.getTransaction().commit();
            return updatedMovie;
        } catch (Exception e)
        {
            throw new ApiException(401, "Error updating movie", e);
        }
    }

    @Override
    public void delete(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, id);
            if (movie == null)
            {
                em.getTransaction().rollback();
                throw new ApiException(401, "Error deleting movie, movie was not found");
            }
            em.remove(movie);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error removing movie", e);
        }
    }
}
