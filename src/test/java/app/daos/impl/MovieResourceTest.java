package app.daos.impl;

import app.config.HibernateConfig;
import app.entities.User;
import app.populator.GlobalPopulator;
import app.populator.PopulatedData;
import app.populator.UserPopulator;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import dk.bugelhartmann.UserDTO;
import groovy.xml.StreamingDOMBuilder;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;

public class MovieResourceTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    List<UserDTO> userDTOS = new ArrayList<>();

    @BeforeEach
    void setup()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            PopulatedData data = GlobalPopulator.populate();

            Arrays.stream(data.directors).forEach(em::persist);
            Arrays.stream(data.genres).forEach(em::persist);
            Arrays.stream(data.actors).forEach(em::persist);
            Arrays.stream(data.movies).forEach(em::persist);

            List<User> userList = UserPopulator.populate();
            userList.forEach(em::persist);
            userDTOS.add(new UserDTO(userList.get(0).getName(), userList.get(0).getPassword()));
            userDTOS.add(new UserDTO(userList.get(1).getName(), userList.get(1).getPassword()));

            em.getTransaction().commit();
        } catch (Exception e)
        {
            e.printStackTrace();
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
                .get("/movies/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void movieByLikedTest()
    {
        given()
                .when()
                .get("/movies/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2));
    }

    @Test
    @DisplayName("Testing user authentication on id and password")
    void testUserIdAndRole()
    {
        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("password", equalTo(BCrypt.hashpw(System.getenv("ADMIN_PASSWORD"),  BCrypt.gensalt()))) //vi skal indsætte et rigtig password her
                .body("roles", hasItem("ADMIN"));
    }
}
