package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.impl.SecurityController;
import app.daos.impl.SecurityDAO;
import app.entities.User;
import app.exceptions.ValidationException;
import app.populator.GlobalPopulator;
import app.populator.PopulatedData;
import app.populator.UserPopulator;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import app.utils.Utils;
import dk.bugelhartmann.UserDTO;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MovieResourceTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private List<UserDTO> userDTOS = new ArrayList<>();
    private UserDTO userDTO, adminDTO;
    private String userToken, adminToken;
    private final static SecurityDAO securityDAO = new SecurityDAO(emf);
    private final static SecurityController securityController = SecurityController.getInstance();

    @BeforeEach
    void setup()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM User u").executeUpdate();
            em.createQuery("DELETE FROM Role r").executeUpdate();
            em.createQuery("DELETE FROM Movie m").executeUpdate();
            em.createQuery("DELETE FROM Genre g").executeUpdate();
            em.createQuery("DELETE FROM Actor a").executeUpdate();
            em.createQuery("DELETE FROM Director d").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE movie_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE genre_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE actor_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE director_id_seq RESTART WITH 1").executeUpdate();

            PopulatedData data = GlobalPopulator.populate();

            Arrays.stream(data.directors).forEach(em::persist);
            Arrays.stream(data.genres).forEach(em::persist);
            Arrays.stream(data.actors).forEach(em::persist);
            Arrays.stream(data.movies).forEach(em::persist);

            List<User> userList = UserPopulator.populateTest();
            userList.forEach(em::persist);
            adminDTO = new UserDTO(userList.get(0).getName(), userList.get(0).getPassword());
            userDTO = new UserDTO(userList.get(1).getName(), userList.get(1).getPassword());

            em.getTransaction().commit();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        boolean deployed = System.getenv("DEPLOYED") != null;

        String adminPassword = deployed ? System.getenv("ADMIN_PASSWORD") : Utils.getPropertyValue("ADMIN_PASSWORD", "config.properties");

        try
        {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), "1234");
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getUsername(), adminPassword);
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (ValidationException ve)
        {
            throw new RuntimeException(ve);
        }

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(Routes.getRoutes(emf))
                .startServer(7777);
        RestAssured.baseURI = "http://localhost:7777/api";
    }

    @AfterEach
    void tearDown()
    {
        ApplicationConfig.stopServer();
    }

    @Test
    @DisplayName("Test getting movie by id")
    void movieByIdTest()
    {
        given()
                .when()
                .get("/movies/movie/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    @DisplayName("Testing for liked movies by id")
    void movieByLikedTest()
    {
        given()
                .when()
                .post("/movies/like/2/1") // POST a movie to user with ID 2, where movie ID is 1
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("title", notNullValue());
    }

    @Test
    @DisplayName("Test fetching user's watch history")
    void testUserHistory()
    {
        given()
                .when()
                .get("/movies/history/2")
                .then()
                .statusCode(200); // Expecting success
    }


    @Test
    @DisplayName("Test for random movie on specific id")
    void testRandomMovie()
    {
        given()
                .when()
                .get("/movies/random/2")
                .then()
                .statusCode(200); //Får en 404 error
    }

    @Test
    @DisplayName("Test")
    void testRandomMovieFromGenre()
    {
        given()
                .when()
                .get("/movies/random-movie/action")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", notNullValue());

    }

}
