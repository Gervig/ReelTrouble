package app.daos.impl;

import app.daos.IDAO;
import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenreDAO implements IDAO<Genre, Long>
{
    private static EntityManagerFactory emf;
    private static GenreDAO instance;

    public static GenreDAO getInstance(EntityManagerFactory _emf)
    {
        if (emf == null)
        {
            emf = _emf;
            instance = new GenreDAO();
        }
        return instance;
    }

    @Override
    public Genre create(Genre genre)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.name =: name", Genre.class);
            query.setParameter("name", genre.getName());

            List<Genre> existingGenres = query.getResultList();

            if (!existingGenres.isEmpty())
                return existingGenres.get(0);

            em.persist(genre);
            em.getTransaction().commit();
            return genre;
        } catch (Exception e)
        {
            throw new ApiException(401, "Error creating genre", e);
        }
    }

    @Override
    public Genre read(Long id)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.find(Genre.class, id);
        }
    }

    @Override
    public List<Genre> readAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error finding list of genres", e);
        }
    }

    public Genre readByApiId(Long apiID)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            try
            {
                return em.createQuery("SELECT g FROM Genre g WHERE g.genreApiId = :genreApiId", Genre.class)
                        .setParameter("genreApiId", apiID)
                        .getSingleResult();
            } catch (NoResultException e)
            {
                return null; // Returnér null hvis ingen genre findes
            }
        }
    }

    public Genre findByName(String name)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT g FROM Genre g " +
                    "WHERE LOWER(g.name) LIKE LOWER(:name)", Genre.class)
                .setParameter("name", "%" + name + "%")
                .getSingleResult();
        }
    }


    @Override
    public Genre update(Genre genre)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            Genre updatedGenre = em.merge(genre);
            em.getTransaction().commit();
            return updatedGenre;
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
            Genre genre = em.find(Genre.class, id);
            if (genre == null)
            {
                em.getTransaction().rollback();
                throw new ApiException(401, "Error deleting genre, genre was not found");
            }
            em.remove(genre);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            throw new ApiException(401, "Error removing genre", e);
        }
    }

    public Map<Long, Genre> getGenreMap() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Genre> genres = em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
            return genres.stream().collect(Collectors.toMap(Genre::getGenreApiId, Function.identity()));
        } catch (Exception e) {
            throw new ApiException(401, "Error finding list of genres", e);
        }
    }

}

