package app.daos.impl;

import app.config.HibernateConfig;
import app.populator.GlobalPopulator;
import app.populator.PopulatedData;
import app.rest.ApplicationConfig;
import app.rest.Routes;
import groovy.xml.StreamingDOMBuilder;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static java.util.function.Predicate.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;

public class MovieResourceTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

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
    @DisplayName("Testing for liked movies by id")
    void movieByLikedTest()
    {
        given()
                .when()
                .post("/like/1/1")
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("title", notNullValue())
                .body("genre", notNullValue());
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
                .body("password", equalTo("hashed_password")) //vi skal indsætte et rigtig password her
                .body("roles", hasItem("ADMIN"));
    }

    @Test
    @DisplayName("Test fetching user's watch history")
    void testUserHistory() {
        given()
                .when()
                .get("/history/1")
                .then()
                .statusCode(200) // Expecting success
                .body("$",hasSize(greaterThan(0)))
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].genre", notNullValue());
    }


    @Test
    @DisplayName("Test for random movie on specific id")
    void testRandomMovie (){
        given()
                .when()
                .get("/random")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", greaterThan(0))
                .body("title", notNullValue())
                .body("genre", notNullValue()); //Får en 404 error
    }

    @Test
    @DisplayName("Test")
    void testRandomMovieFromGenre (){
        given()
                .when()
                .get("/random-movie")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", notNullValue())
                .body("genre", notNullValue()); //Får en 404 error

    }

}
