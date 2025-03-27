package app;

import app.callable.DetailsServiceCallable;
import app.config.HibernateConfig;
import app.daos.impl.ActorDAO;
import app.daos.impl.DirectorDAO;
import app.daos.impl.GenreDAO;
import app.daos.impl.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import app.services.EntityService;
import app.services.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        MovieDAO movieDAO = MovieDAO.getInstance(emf);
        ActorDAO actorDAO = ActorDAO.getInstance(emf);
        GenreDAO genreDAO = GenreDAO.getInstance(emf);
        DirectorDAO directorDAO = DirectorDAO.getInstance(emf);

        List<String> movieApiIds = Service.getMovieApiIds();

        System.out.println("Total amount of movie IDs fetched: " + movieApiIds.size());

        List<MovieDTO> movieDTOS = DetailsServiceCallable.getMovieDTOs(movieApiIds);

        System.out.println("Total amount of MovieDTOs created: " + movieDTOS.size());

        movieDTOS.forEach(System.out::println);

        movieDTOS.forEach(EntityService::persistMovie);

//        ApplicationConfig
//                .getInstance()
//                .initiateServer()
////                .securityCheck() //TODO add authenticate and authorize calls
//                .setRoute(Routes.getRoutes(emf))
//                .handleException()
//                .startServer(7074); //TODO change this to an available port for deployment

    }

}
