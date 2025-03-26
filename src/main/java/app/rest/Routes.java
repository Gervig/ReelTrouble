package app.rest;

import app.controllers.impl.MovieController;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Routes
{
    private static MovieController movieController;
    private static Logger logger = LoggerFactory.getLogger(Routes.class);

    public static EndpointGroup getRoutes(EntityManagerFactory emf)
    {
        movieController = new MovieController(emf);
        return () ->
        {
            path("streamfinder", () ->
                    {
                        get("/", ctx ->
                                {
                                    logger.info("Information about the resource that was accessed: " + ctx.path());
//                                    List<MediaDTO> mediaDTOS = mediaController.readAll(); //TODO
                                });
                    });
        };
    }

}