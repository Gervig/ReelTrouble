package app.rest;

import app.controllers.impl.MediaController;
import app.dtos.ErrorMessage;
import app.dtos.MediaDTO;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Routes
{
    private static MediaController mediaController;
    private static Logger logger = LoggerFactory.getLogger(Routes.class);

    public static EndpointGroup getRoutes(EntityManagerFactory emf)
    {
        mediaController = new MediaController(emf);
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