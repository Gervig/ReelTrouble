package app;

import app.callable.DetailsServiceCallable;
import app.config.HibernateConfig;
import app.dtos.MovieDTO;
import app.rest.ApplicationConfig;
import app.rest.Routes;
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

        List<String> movieApiIds = Service.getMovieApiIds();

        System.out.println("Total amount of movie IDs fetched: " + movieApiIds.size());

        List<MovieDTO> movieDTOS = DetailsServiceCallable.getMovieDTOs(movieApiIds);

        movieDTOS.forEach(System.out::println);

//        ApplicationConfig
//                .getInstance()
//                .initiateServer()
////                .securityCheck() //TODO add authenticate and authorize calls
//                .setRoute(Routes.getRoutes(emf))
//                .handleException()
//                .startServer(7070); //TODO change this to an available port for deployment

    }

}
