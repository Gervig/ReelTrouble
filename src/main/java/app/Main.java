package app;

import app.callable.DetailsServiceCallable;
import app.config.HibernateConfig;
import app.daos.RoleDAO;
import app.daos.UserDAO;
import app.daos.impl.*;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.entities.Movie;
import app.entities.Role;
import app.entities.User;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import app.services.EntityService;
import app.services.Service;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main
{
    public static void main(String[] args)
    {
        // TODO clean up main!
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        MovieDAO movieDAO = MovieDAO.getInstance(emf);
        ActorDAO actorDAO = ActorDAO.getInstance(emf);
        GenreDAO genreDAO = GenreDAO.getInstance(emf);
        DirectorDAO directorDAO = DirectorDAO.getInstance(emf);
        RoleDAO roleDAO = RoleDAO.getInstance(emf);
        UserDAO userDAO = UserDAO.getInstance(emf);
        SecurityDAO securityDAO = new SecurityDAO(emf);

        User admin = new User(System.getenv("ADMIN_NAME"), System.getenv("ADMIN_PASSWORD"));
        Role adminRole = new Role("ADMIN");
        admin.addRole(adminRole);

        try(EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(adminRole);
            em.persist(admin);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            throw new RuntimeException();
        }

        List<String> movieApiIds = Service.getMovieApiIds();

        System.out.println("Total amount of movie IDs fetched: " + movieApiIds.size());

        List<MovieDTO> movieDTOS = DetailsServiceCallable.getMovieDTOs(movieApiIds);

        System.out.println("Total amount of MovieDTOs created: " + movieDTOS.size());

        List<Movie> movies = EntityService.persistMovies(movieDTOS);

        System.out.println("Total amount of Movies persisted: " + movies.size());

        //TODO rewrite persistMovie method to work on a list
//        movieDTOS.forEach(EntityService::persistMovie);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .securityCheck() //TODO add authenticate and authorize calls
                .setRoute(Routes.getRoutes(emf))
                .handleException()
                .startServer(7074); //TODO change this to an available port for deployment

    }

}
