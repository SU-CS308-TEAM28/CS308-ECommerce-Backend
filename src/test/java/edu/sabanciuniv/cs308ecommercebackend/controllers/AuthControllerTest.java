package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth.AuthLogin;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth.AuthRegister;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JWTUtils jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    public void register_ShouldReturnSuccess() throws Exception {
        AuthRegister.Request request = new AuthRegister.Request("John", "Doe", "john@example.com", "password", new Date());

        lenient().when(userService.createAccount(anyString(), anyString(), anyString(), anyString(), any(Date.class))).thenReturn(edu.sabanciuniv.cs308ecommercebackend.models.User.builder().email("john@example.com").build());

        var response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("User john@example.com registered successfully.");
    }

    @Test
    public void register_ShouldReturnBadRequest_WhenExceptionOccurs() throws Exception {
        AuthRegister.Request request = new AuthRegister.Request("John", "Doe", "john@example.com", "password", new Date());

        lenient().when(userService.createAccount(anyString(), anyString(), anyString(), anyString(), any(Date.class))).thenThrow(new RuntimeException());

        var response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Unknown error occurred while creating account.");
    }

    @Test
    public void login_ShouldReturnSuccess() {
        AuthLogin.Request request = new AuthLogin.Request("john@example.com", "password");
        edu.sabanciuniv.cs308ecommercebackend.models.User user = edu.sabanciuniv.cs308ecommercebackend.models.User.builder().email("john@example.com").build();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("john@example.com").password("password").roles("USER").build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken("john@example.com", "password"));
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken("john@example.com")).thenReturn("token");
        when(userService.getUserByEmail("john@example.com")).thenReturn(user);

        var response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("User logged in successfully.");
        assertThat(((AuthLogin.Response) response.getBody().get("data")).getToken()).isEqualTo("token");
    }

    @Test
    public void login_ShouldReturnBadRequest_WhenBadCredentials() {
        AuthLogin.Request request = new AuthLogin.Request("john@example.com", "wrongpassword");

        doThrow(new BadCredentialsException("")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        var response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Invalid email or password!");
    }

    @Test
    public void checkAuthentication_ShouldReturnSuccess() {
        edu.sabanciuniv.cs308ecommercebackend.models.User user = edu.sabanciuniv.cs308ecommercebackend.models.User.builder().email("john@example.com").build();
        when(userService.getUserByToken("Bearer token")).thenReturn(user);

        var response = authController.checkAuthentication("Bearer token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("Authentication valid, logged in as john@example.com");
    }
}
