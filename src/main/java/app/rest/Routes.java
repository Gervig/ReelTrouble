package app.rest;

import app.controllers.ISecurityController;
import app.controllers.impl.MovieController;
import app.controllers.impl.SecurityController;
import app.controllers.impl.UserController;
import app.dtos.MovieDTO;
import app.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import static io.javalin.apibuilder.ApiBuilder.*;


public class Routes
{
    private static ISecurityController securityController = new SecurityController();
    private static MovieController movieController;
    private static UserController userController;
    private static Logger logger = LoggerFactory.getLogger(Routes.class);

    public static EndpointGroup getRoutes(EntityManagerFactory emf)
    {
        movieController = new MovieController(emf);
        userController = new UserController(emf);

        return () ->
        {
            path("auth", () ->
            {
                get("healthcheck", securityController::healthCheck, Role.ANYONE);
                post("register", securityController.register(), Role.ANYONE);
                post("login", securityController.login(), Role.ANYONE);
            });

            //Admins can add new movies to the DB - NOT CHECKED
            path("admin", () ->
            {
                post("movies/add", ctx ->
                {
                    MovieDTO movie = ctx.bodyAsClass(MovieDTO.class);
                    MovieDTO created = movieController.addNewMovieToDB(movie);
                    ctx.json(created);
                }, Role.ADMIN);
            });

            //TODO sikre at userID url er secured i backend (SE MAIL FRA JON AF 30-03-2025)
            path("movies", () ->
            {
                //Shows all movies - CHECKED
                get("", ctx ->
                {
                    List<MovieDTO> movies = movieController.getAll();
                    ctx.json(movies);
                }, Role.ANYONE);
                //Show 1 movie with a given id - CHECKED
                get("movie/{id}", ctx ->
                {
                    Long movieId = Long.parseLong(ctx.pathParam("id"));
                    MovieDTO movieDTO = movieController.getById(movieId);
                    ctx.json(movieDTO);
                }, Role.ANYONE);
                //All movies in a genre - CHECKED
                get("genre/{genre}", ctx ->
                {
                    String genre = ctx.pathParam("genre").replace('-', ' ');
                    List<MovieDTO> movies = movieController.getMoviesInGenre(genre);
                    ctx.json(movies);
                }, Role.ANYONE);
                //Movie in genre not on users list - CHECKED
                get("recommend/{genre}/{username}", ctx ->
                {
                    String genre = ctx.pathParam("genre");
                    String username = ctx.pathParam("username");
                    MovieDTO movie = movieController.getRandomMovieExclUsersListWithGenre(genre, username);
                    ctx.json(movie);
                }, Role.USER);
                //Users list ** BY USERNAME **
                get("history/{username}", ctx ->
                {
                    String username = ctx.pathParam("username");
                    List<MovieDTO> movies = movieController.getAllMoviesOnUsersList(username);
                    ctx.json(movies);
                }, Role.USER);
                //Gets a random movie based on nothing but genre - CHECKED
                get("random-movie/{genre}", ctx ->
                {
                    String genre = ctx.pathParam("genre");
                    MovieDTO movie = movieController.getRandomMovieInGenre(genre);
                    ctx.json(movie);
                }, Role.ANYONE);
                //Add a movie to a user's liked list ** BY USERNAME **
                post("like/{username}/{movieId}", ctx ->
                {
                    String username = ctx.pathParam("username");
                    Long movieId = Long.parseLong(ctx.pathParam("movieId"));
                    MovieDTO movie = userController.postMovieToUsersList(movieId, username);
                    ctx.json(movie).status(201);
                }, Role.USER);
                //TODO check this in demo.http
                //Show 1 random movie not liked by the user - NOT CHECKED
                get("random/{username}", ctx -> {
                    String username = ctx.pathParam("username");
                    MovieDTO movie = movieController.getRandomMovieExclUsersList(username);
                    ctx.json(movie);
                }, Role.USER);
            });
        };
    }
}