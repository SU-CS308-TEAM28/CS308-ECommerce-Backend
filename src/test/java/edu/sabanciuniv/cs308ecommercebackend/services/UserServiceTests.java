package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Activate when required.
public class UserServiceTests
{

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void doesUserServiceCreateUser()
    {
        // Check if context required is loaded.
        assert userService != null;
        assert userRepository != null;

        // Check if the test unit user isn't already on MongoDB.
        assert userRepository.findByEmail("test@unit.run") == null;

        // Check if user creation is working.
        User createdUser = null;
        try
        {
            createdUser = userService.createAccount(
                    "Test",
                    "Run",
                    "test@unit.run",
                    "testunitrun",
                    Date.from(Instant.now())
            );
        }
        catch (Exception e) { assert false; }
        assert userRepository.findByEmail("test@unit.run") != null;

        // Clean-up
        userRepository.delete(createdUser);
    }

}
