package app;

import app.callable.DetailsServiceCallable;
import app.config.HibernateConfig;
import app.daos.UserDAO;
import app.daos.impl.ActorDAO;
import app.daos.impl.DirectorDAO;
import app.daos.impl.GenreDAO;
import app.daos.impl.MovieDAO;
import app.dtos.MovieDTO;
import app.entities.Director;
import app.entities.Role;
import app.entities.User;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import app.services.EntityService;
import app.services.Service;
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
        EntityManager em = emf.createEntityManager();

        MovieDAO movieDAO = MovieDAO.getInstance(emf);
        ActorDAO actorDAO = ActorDAO.getInstance(emf);
        GenreDAO genreDAO = GenreDAO.getInstance(emf);
        DirectorDAO directorDAO = DirectorDAO.getInstance(emf);

        User admin = User.builder()
                .created(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(new Role("ADMIN"))))
                .name(System.getenv("ADMIN_NAME"))
                .password(System.getenv("ADMIN_PASSWORD"))
                .build();

        UserDAO userDAO = UserDAO.getInstance(emf);

        userDAO.create(admin);

        List<String> movieApiIds = Service.getMovieApiIds();

        System.out.println("Total amount of movie IDs fetched: " + movieApiIds.size());

        List<MovieDTO> movieDTOS = DetailsServiceCallable.getMovieDTOs(movieApiIds);

        System.out.println("Total amount of MovieDTOs created: " + movieDTOS.size());

        movieDTOS.forEach(System.out::println);

        //TODO rewrite persistMovie method to work on a list
        movieDTOS.forEach(EntityService::persistMovie);

//        ApplicationConfig
//                .getInstance()
//                .initiateServer()
//                .securityCheck() //TODO add authenticate and authorize calls
//                .setRoute(Routes.getRoutes(emf))
//                .handleException()
//                .startServer(7074); //TODO change this to an available port for deployment

    }

}
