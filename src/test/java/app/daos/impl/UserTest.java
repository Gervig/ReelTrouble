package app.daos.impl;

import app.config.HibernateConfig;
import app.entities.Role;
import app.entities.User;
import app.services.EntityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class LoginTest {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final EntityService userService = Mockito.mock(EntityService.class); // Mock EntityService aka laver falsk data

    private User testUser;

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Clear existing users and roles
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();

            // Create and persist the admin role
            Role adminRole = new Role("ADMIN");
            em.persist(adminRole); // Persist role before assigning it to the user

            // Create a test user with hashed password
            testUser = new User();
            testUser.setName("testUser");
            testUser.setPassword(BCrypt.hashpw("securePassword", BCrypt.gensalt())); // Hash password
            testUser.setRoles(new HashSet<>(Set.of(adminRole)));

            em.persist(testUser); // Persist the user
            em.getTransaction().commit();
        }
    }


    @Test
    void testSuccessfulLogin() {
        // Mock UserService to return our testUser
        when(userService.authenticate(1L, "securePassword")).thenReturn(testUser);

        User loggedInUser = userService.authenticate(1L, "securePassword");

        assertNotNull(loggedInUser, "User should be authenticated");
        assertEquals(1L, loggedInUser.getName(), "Usernames should match");
        assertEquals("ADMIN", loggedInUser.getRoles(), "User should have ADMIN role");
    }

    @Test
    void testFailedLogin_WrongPassword() {
        when(userService.authenticate(1L, "wrongPassword")).thenReturn(null);

        User loggedInUser = userService.authenticate(1L, "wrongPassword");

        assertNull(loggedInUser, "User authentication should fail with wrong password");
    }

    @Test
    void testFailedLogin_NonExistentUser() {
        when(userService.authenticate(2L, "anyPassword")).thenReturn(null);

        User loggedInUser = userService.authenticate(2L, "anyPassword");

        assertNull(loggedInUser, "Authentication should fail for non-existent users");
    }
}
