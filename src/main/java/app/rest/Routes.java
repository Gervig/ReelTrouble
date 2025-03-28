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
                //todo: ENDPOINT GET NOT FOUND - jeg ved ikke hvordan jeg skal debugge dette?
                post("register", securityController.register(), Role.ANYONE);
                post("login", securityController.login(), Role.ANYONE);
            });

            //Admins can add new movies to the DB - NOT CHECKED
            path("/admin", () ->
            {
                post("/movies/add", ctx ->
                {
                    MovieDTO movie = ctx.bodyAsClass(MovieDTO.class);
                    MovieDTO created = movieController.addNewMovieToDB(movie);
                    ctx.json(created);
                }, Role.ADMIN);
            });

            path("movies", () ->
            {
                //Shows all movies - CHECKED
                get("/", ctx ->
                {
                    List<MovieDTO> movies = movieController.getAll();
                    ctx.json(movies);
                }, Role.ANYONE);
                //Show 1 movie with a given id - CHECKED
                get("/movie/{id}", ctx ->
                {
                    Long movieId = Long.parseLong(ctx.pathParam("id"));
                    MovieDTO movieDTO = movieController.getById(movieId);
                    ctx.json(movieDTO);
                }, Role.ANYONE);
                //All movies in a genre - CHECKED
                get("/genre/{genre}", ctx ->
                {
                    String genre = ctx.pathParam("genre").replace('-', ' ');
                    List<MovieDTO> movies = movieController.getMoviesInGenre(genre);
                    ctx.json(movies);
                }, Role.ANYONE);
                //Movie in genre not on users list - NOT CHECKED
                get("/recommend/{genre}/{id}", ctx ->
                {
                    String genre = ctx.pathParam("genre");
                    Long userId = Long.parseLong(ctx.pathParam("id"));
                    MovieDTO movie = movieController.getRandomMovieExclUsersListWithGenre(genre, userId);
                    ctx.json(movie);
                }, Role.USER);
                //Users list - NOT CHECKED
                get("/history/{id}", ctx ->
                {
                    Long userId = Long.parseLong(ctx.pathParam("id"));
                    List<MovieDTO> movies = movieController.getAllMoviesOnUsersList(userId);
                    ctx.json(movies);
                }, Role.USER);
                //Gets a random movie based on nothing but genre - CHECKED
                get("/random-movie/{genre}", ctx ->
                {
                    String genre = ctx.pathParam("genre");
                    MovieDTO movie = movieController.getRandomMovieInGenre(genre);
                    ctx.json(movie);
                }, Role.ANYONE);
                //Add a movie to a user's liked list - NOT CHECKED
                post("/like/{id}/{movieId}", ctx ->
                {
                    Long userId = Long.parseLong(ctx.pathParam("id"));
                    Long movieId = Long.parseLong(ctx.pathParam("movieId"));
                    userController.postMovieToUsersList(movieId, userId);
                    MovieDTO movie = userController.postMovieToUsersList(movieId, userId);
                    ctx.json(movie).status(201);
                }, Role.USER);
                //Show 1 random movie not liked by the user - NOT CHECKED
                get("/random/{id}", ctx -> {
                    Long userId = Long.parseLong(ctx.pathParam("id"));
                    MovieDTO movie = movieController.getRandomMovieExclUsersList(userId);
                    ctx.json(movie);
                }, Role.USER);
            });
        };
    }
}