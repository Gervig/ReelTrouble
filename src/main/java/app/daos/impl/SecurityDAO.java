package app.daos.impl;

import app.daos.ISecurityDAO;
import app.entities.Role;
import app.entities.User;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.*;

import java.util.stream.Collectors;

public class SecurityDAO implements ISecurityDAO
{

    private static ISecurityDAO instance;
    private static EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf)
    {
        emf = _emf;
    }

    private EntityManager getEntityManager()
    {
        return emf.createEntityManager();
    }

    @Override
    public UserDTO getVerifiedUser(String username, String password) throws ValidationException
    {
        try (EntityManager em = getEntityManager())
        {
            User user = readByName(username);
            if (user == null)
            {
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            }            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password))
            {
                throw new ValidationException("Wrong password");
            }
            return new UserDTO(user.getName(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }

    public User readByName(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.name = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();
            return user;
        } catch (Exception e) {
            throw new ApiException(404, "Error could not find user with name " + username, e);
        }
    }

    @Override
    public User createUser(String username, String password)
    {
        try (EntityManager em = getEntityManager())
        {
            User userEntity = em.find(User.class, username);
            if (userEntity != null)
            {
                throw new EntityExistsException("User with username: " + username + " already exists");
            }
            userEntity = new User(username, password);
            em.getTransaction().begin();
            Role userRole = em.find(Role.class, "user");
            if (userRole == null)
            {
                userRole = new Role("user");
            }
            em.persist(userRole);
            userEntity.addRole(userRole);
            em.persist(userEntity);
            em.getTransaction().commit();
            return userEntity;
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public User addRole(UserDTO userDTO, String newRole)
    {
        try (EntityManager em = getEntityManager())
        {
            User user = em.find(User.class, userDTO.getUsername());
            if (user == null)
            {
                throw new EntityNotFoundException("No user found with username: " + userDTO.getUsername());
            }            em.getTransaction().begin();
            Role role = em.find(Role.class, newRole);
            if (role == null)
            {
                role = new Role(newRole);
                em.persist(role);
            }
            user.addRole(role);
            em.getTransaction().commit();
            return user;
        }
    }
}

