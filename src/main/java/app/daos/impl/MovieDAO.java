package app.daos.impl;

import app.daos.IDAO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                throw new ApiException(401, "Error creating movie " + movie.getMovieApiId(), e);
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

    public List<Movie> findMoviesByGenre(Long genreID)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "JOIN m.genres g " +
                                    "WHERE g.id = :genreID",
                            Movie.class)
                    .setParameter("genreID", genreID)
                    .getResultList();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding movies with genreID: " + genreID, e);
        }
    }

    public List<Movie> findMovieExclUsersListWithGenre(String genre, Long userId)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<Movie> movies = em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "JOIN m.genres g " +
                                    "WHERE LOWER(g.name) = LOWER(:genre) " +
                                    "AND m NOT IN (SELECT mu FROM User u JOIN u.likeList mu WHERE u.id = :userId)",
                            Movie.class)
                    .setParameter("genre", genre)
                    .setParameter("userId", userId)
                    .getResultList();

            if (movies.isEmpty())
            {
                throw new ApiException(404, "No available movies found for genre: " + genre);
            }
            return movies;
        }
    }

    public List<Movie> findMoviesExclUsersList(Long userId)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<Movie> movies = em.createQuery(
                            "SELECT m FROM Movie m WHERE m NOT IN " +
                                    "(SELECT m2 FROM User u JOIN u.likeList m2 WHERE u.id = :userId)",
                            Movie.class)
                    .setParameter("userId", userId)
                    .getResultList();
            return movies;
        }
    }

    public List<Movie> findMovieInclUsersList(Long userId)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<Movie> movies = em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "WHERE m IN (SELECT mu FROM User u JOIN u.likeList mu WHERE u.id = :userId)",
                            Movie.class)
                    .setParameter("userId", userId)
                    .getResultList();
            return movies;
        }
    }

    public List<Movie> readWithDetailsByTitle(String title)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "LEFT JOIN FETCH m.actors " +
                                    "LEFT JOIN FETCH m.genres " +
                                    "LEFT JOIN FETCH m.directors " +
                                    "WHERE LOWER(m.title) LIKE LOWER(:title)", Movie.class)
                    .setParameter("title", "%" + title + "%")
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

    public Map<Long, Movie> getMovieMap()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            List<Movie> movies = em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
            return movies.stream().collect(Collectors.toMap(Movie::getMovieApiId, Function.identity()));
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding list of movies", e);
        }
    }

    public Movie merge(Movie movie) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Save all actors first to avoid transient exception
            Set<Actor> managedActors = new HashSet<>();
            for (Actor actor : movie.getActors()) {
                if (actor.getId() == null) {
                    em.persist(actor); // New actors are saved first
                } else {
                    actor = em.merge(actor); // Merge existing ones
                }
                managedActors.add(actor);
            }
            movie.setActors(managedActors); // Ensure movie has managed actors

            Set<Director> managedDirectors = new HashSet<>();
            for (Director director : movie.getDirectors()) {
                if (director.getId() == null) {
                    em.persist(director);
                } else {
                    director = em.merge(director);
                }
                managedDirectors.add(director);
            }
            movie.setDirectors(managedDirectors);

            Set<Genre> managedGenres = new HashSet<>();
            for (Genre genre : movie.getGenres()) {
                if (genre.getId() == null) {
                    em.persist(genre);
                } else {
                    genre = em.merge(genre);
                }
                managedGenres.add(genre);
            }
            movie.setGenres(managedGenres);

            // Merge the movie after actors are saved
            movie = em.merge(movie);

            em.getTransaction().commit();
            return movie;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new ApiException(500, "Error saving Movie", e);
        }
    }


}
