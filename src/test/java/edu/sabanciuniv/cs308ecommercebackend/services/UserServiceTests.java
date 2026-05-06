package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests
{

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Test
    void doesUserServiceCreateUser() throws Exception
    {
        Date birthDate = Date.from(Instant.now());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(passwordEncoder.encode("testunitrun")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createAccount(
                "Test",
                "Run",
                "test@unit.run",
                "testunitrun",
                birthDate
        );

        verify(userRepository).save(userCaptor.capture());

        assert createdUser != null;
        assert createdUser.getEmail().equals("test@unit.run");
        assert userCaptor.getValue().getPwdHash().equals("encoded-password");
    }

    @Test
    void doesUserServiceCreateUserAssignDefaultUserType() throws Exception
    {
        when(passwordEncoder.encode("testunitrun")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createAccount(
                "Test",
                "Run",
                "test@unit.run",
                "testunitrun",
                Date.from(Instant.now())
        );

        assert createdUser != null;
        assert createdUser.getUserType().equals("user");
    }

    @Test
    void doesUserServiceCreateUserInitializeUserData() throws Exception
    {
        when(passwordEncoder.encode("testunitrun")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createAccount(
                "Test",
                "Run",
                "test@unit.run",
                "testunitrun",
                Date.from(Instant.now())
        );

        assert createdUser != null;
        assert createdUser.getUserData() != null;
        assert createdUser.getUserData().getWishlist() != null;
        assert createdUser.getUserData().getWishlist().isEmpty();
        assert createdUser.getUserData().getShoppingCart() != null;
        assert createdUser.getUserData().getShoppingCart().isEmpty();
    }

    @Test
    void doesGetUserByEmailClearPasswordHash()
    {
        User user = User.builder()
                .email("test@unit.run")
                .pwdHash("encoded-password")
                .build();

        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);

        User responseUser = userService.getUserByEmail("test@unit.run");

        assert responseUser != null;
        assert responseUser.getEmail().equals("test@unit.run");
        assert responseUser.getPwdHash().isEmpty();
    }

    @Test
    void doesGetUserByTokenSupportBearerPrefix()
    {
        User user = User.builder()
                .email("test@unit.run")
                .pwdHash("encoded-password")
                .build();

        when(jwtUtils.extractUsername("token-value")).thenReturn("test@unit.run");
        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);

        User responseUser = userService.getUserByToken("Bearer token-value");

        assert responseUser != null;
        assert responseUser.getEmail().equals("test@unit.run");
        assert responseUser.getPwdHash().isEmpty();
    }

}
