package app.populator;

import app.entities.Role;
import app.entities.User;

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
        Role adminRole = new Role("ADMIN");
        User admin = User.builder()
                .created(LocalDateTime.now())
                .name(System.getenv("ADMIN_NAME"))
                .password(System.getenv("ADMIN_PASSWORD"))
                .build();
        admin.addRole(adminRole);
        userList.add(admin);

        Role userRole = new Role("USER");
        User user = User.builder()
                .created(LocalDateTime.now())
                .name("User1")
                .password("1234")
                .build();
        user.addRole(userRole);
        userList.add(user);

        return userList;
    }
}
