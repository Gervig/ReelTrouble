package app;

import app.callable.DetailsServiceCallable;
import app.config.HibernateConfig;
import app.daos.RoleDAO;
import app.daos.UserDAO;
import app.daos.impl.*;
import app.dtos.MovieDTO;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import app.services.EntityService;
import app.services.Service;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        setupDatabase(emf);

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .securityCheck()
                .setRoute(Routes.getRoutes(emf))
                .handleException()
                .startServer(7074); //TODO change this to an available port for deployment

    }

    private static void setupDatabase(EntityManagerFactory emf)
    {
        // instantiates all the emfs inside all the DAO classes
        MovieDAO movieDAO = MovieDAO.getInstance(emf);
        ActorDAO actorDAO = ActorDAO.getInstance(emf);
        GenreDAO genreDAO = GenreDAO.getInstance(emf);
        DirectorDAO directorDAO = DirectorDAO.getInstance(emf);
        RoleDAO roleDAO = RoleDAO.getInstance(emf);
        UserDAO userDAO = UserDAO.getInstance(emf);
        SecurityDAO securityDAO = new SecurityDAO(emf);

        // creates an admin in the database
        //TODO fix admin, for now create admin manually in PGAdmin
//        UserPopulator.createAdmin(emf);

        // fetches all the TMDB IDs for a select range of movies
        List<String> movieApiIds = Service.getMovieApiIds();

        System.out.println("Total amount of movie IDs fetched: " + movieApiIds.size());

        // fetches details for each movie and creates DTOs
        List<MovieDTO> movieDTOS = DetailsServiceCallable.getMovieDTOs(movieApiIds);

        System.out.println("Total amount of MovieDTOs created: " + movieDTOS.size());

        // converts DTOs to Entities and persists them in the database
        EntityService.persistMovies(movieDTOS);
    }

}
