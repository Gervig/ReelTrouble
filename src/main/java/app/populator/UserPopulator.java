package app.populator;

import app.entities.Role;
import app.entities.User;
import app.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserPopulator
{
    public static void createAdmin(EntityManagerFactory emf)
    {
        boolean deployed = System.getenv("DEPLOYED") != null;

        String adminName = deployed ? System.getenv("ADMIN_NAME") : Utils.getPropertyValue("ADMIN_NAME", "config.properties");
        String adminPassword = deployed ? System.getenv("ADMIN_PASSWORD") : Utils.getPropertyValue("ADMIN_PASSWORD", "config.properties");

        User admin = new User(adminName, adminPassword);
        Role adminRole = new Role("admin");
        admin.addRole(adminRole);

        try(EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(adminRole);
            em.persist(admin);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            throw new RuntimeException();
        }
    }

    public static List<User> populateTest() {
        List<User> userList = new ArrayList<>();

        boolean deployed = System.getenv("DEPLOYED") != null;

        String adminName = deployed ? System.getenv("ADMIN_NAME") : Utils.getPropertyValue("ADMIN_NAME", "config.properties");
        String adminPassword = deployed ? System.getenv("ADMIN_PASSWORD") : Utils.getPropertyValue("ADMIN_PASSWORD", "config.properties");

        // Debugging: Print values to GitHub Actions logs
        System.out.println("ADMIN_NAME: " + adminName);
        System.out.println("ADMIN_PASSWORD: " + adminPassword);

        if (adminName == null || adminPassword == null) {
            throw new RuntimeException("Admin credentials are missing!");
        }

        User admin = new User(adminName, adminPassword);
        Role adminRole = new Role("admin");
        admin.addRole(adminRole);
        userList.add(admin);

        User user = new User("User1", "1234");
        Role userRole = new Role("user");
        user.addRole(userRole);
        userList.add(user);

        return userList;
    }
}
