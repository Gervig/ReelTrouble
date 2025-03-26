package app.rest;

import app.controllers.impl.MovieController;
import app.controllers.securityController.ISecurityController;
import app.controllers.securityController.SecurityController;
import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.enums.Role;
import com.fasterxml.jackson.core.io.BigDecimalParser;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.security.RouteRole;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Routes
{
    private static ISecurityController securityController = new SecurityController();
    private static MovieController movieController;
    private static Logger logger = LoggerFactory.getLogger(Routes.class);

    public static EndpointGroup getRoutes(EntityManagerFactory emf)
    {
        movieController = new MovieController(emf);
        return () ->
        {
            path("auth", () ->
            {
                //Fejl pga. enum
                post("register", securityController.register(), Role.ANYONE);
                post("login", securityController.login(), Role.ANYONE);
            });

            //Admins can add new movies to the DB
            path("/admin", () ->
            {
                post("/movies/add", ctx ->
                {
                    String movieTitle = ctx.queryParam("title");
                    LocalDate releaseDate = LocalDate.parse(ctx.queryParam("releasedate"));
                    String description = ctx.queryParam("description");
                    BigDecimal imdbRating = ctx.pathParam(imdbRating);
                    Time duration = ctx.queryParams(duration);

                    //TODO: Skal laves om til objekter til liste
                    List<GenreDTO> genre = ctx.queryParam("genre");
                    //Skal laves om til objekter til liste
                    List<ActorDTO> actor = ctx.queryParam("actor");
                    //Skal laves om til objekter til liste
                    List<DirectorDTO> director = ctx.queryParam("director");

                    MovieDTO movie = movieController.addNewMovieToDB(movie);
                    ctx.json(movie);
                }, Role.ADMIN);
            });

            path("movies", () ->
            {
                //Movie in genre not on users list
                get("/recommend", ctx ->
                {
                    String genre = ctx.queryParam("genre");
                    Long userId = Long.parseLong(ctx.queryParam("userId"));
                    MovieDTO movie = movieController.getRandomMovieExclUsersList(genre, userId);
                    ctx.json(movie);
                }, Role.USER);
                //Users list
                get("/history", ctx ->
                {
                    Long userId = Long.parseLong(ctx.queryParam("userId"));
                    List<MovieDTO> movies = movieController.getAllMoviesOnUsersList(userId);
                    ctx.json(movies);
                }, Role.USER);
                //Gets a random movie based on nothing
                // TODO: not correct right now, as it is secured, i think
                get("/random-movie", ctx ->
                {
                    String genre = ctx.queryParam("genre");
                    MovieDTO movie = movieController.getRandomMovieInGenre(genre);
                    ctx.json(movie);
                }, Role.ANYONE);
                //SHOULD add a movie to a user's liked list, but it needs some more stuff in the controller
                post("like/{id}", ctx ->
                {
                    Long userId = Long.parseLong(ctx.queryParam("userId"));
                    Long movieId = Long.parseLong(ctx.pathParam("id"));
                    MovieDTO movie = movieController.postMovieToUsersList(movieId, userId);
                }, Role.USER);
            });
        };
    }
}