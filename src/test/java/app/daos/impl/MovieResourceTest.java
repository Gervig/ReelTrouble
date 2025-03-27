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

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;

public class MovieResourceTest
{

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    @BeforeEach
    void setup()
    {
        GlobalPopulator.populate();
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
                .get("/movie/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void movieByLikedTest(){
        given()
                .when()
                .get("/movie/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2));
    }

    @Test
    @DisplayName("Testing user authentication on id and password")
    void testUserIdAndRole () {
        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("password",equalTo("hashed_password")) //vi skal indsætte et rigtig password her
                .body("roles", hasItem("ADMIN"));
    }

}
