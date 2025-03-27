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
        User admin = User.builder()
                .created(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(new Role("ADMIN"))))
                .name(System.getenv("ADMIN_NAME"))
                .password(System.getenv("ADMIN_PASSWORD"))
                .build();
        userList.add(admin);

        User user = User.builder()
                .created(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(new Role("USER"))))
                .name("User1")
                .password("1234")
                .build();
        userList.add(user);

        return userList;
    }
}
