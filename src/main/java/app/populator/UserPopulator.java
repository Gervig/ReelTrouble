package app.populator;

import app.entities.Role;
import app.entities.User;
import app.utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserPopulator
{
    public static List<User> populate()
    {
        List<User> userList = new ArrayList<>();

        String adminName = Utils.getPropertyValue("ADMIN_NAME", "config.properties");
        String adminPassword = Utils.getPropertyValue("ADMIN_PASSWORD", "config.properties");
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
