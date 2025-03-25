package app;

import app.config.HibernateConfig;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Main
{
    public static void main(String[] args)
    {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

//        ApplicationConfig
//                .getInstance()
//                .initiateServer()
////                .securityCheck() //TODO add authenticate and authorize calls
//                .setRoute(Routes.getRoutes(emf))
//                .handleException()
//                .startServer(7070); //TODO change this to an available port for deployment

    }

}
