package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth.AuthLogin;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth.AuthRegister;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests
{

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JWTUtils jwtUtil;

    @Mock
    private HttpServletResponse servletResponse;

    @InjectMocks
    private AuthController authController;

    @Test
    void doesRegisterReturnSuccessWhenAccountCreated() throws Exception
    {
        AuthRegister.Request request = new AuthRegister.Request(
                "John",
                "Doe",
                "john@unit.run",
                "secret",
                new Date()
        );

        when(userService.createAccount(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getBirthDate()
        )).thenReturn(buildUser(request.getEmail()));

        var response = authController.register(request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("User john@unit.run registered successfully.", body.get("message"));
        verify(userService).createAccount(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getBirthDate()
        );
    }

    @Test
    void doesRegisterReturnBadRequestWhenServiceThrows() throws Exception
    {
        AuthRegister.Request request = new AuthRegister.Request(
                "John",
                "Doe",
                "john@unit.run",
                "secret",
                new Date()
        );

        when(userService.createAccount(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getBirthDate()
        )).thenThrow(new RuntimeException("creation failed"));

        var response = authController.register(request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Unknown error occurred while creating account.", body.get("message"));
    }

    @Test
    void doesLoginReturnBadRequestForInvalidCredentials()
    {
        AuthLogin.Request request = new AuthLogin.Request("john@unit.run", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("invalid credentials"));

        var response = authController.login(request, servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Invalid email or password!", body.get("message"));
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(servletResponse, never()).addHeader(any(), any());
    }

    @Test
    void doesLoginReturnTokenUserAndCookieWhenCredentialsAreValid()
    {
        AuthLogin.Request request = new AuthLogin.Request("john@unit.run", "secret");
        User user = buildUser(request.getEmail());
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(request.getEmail())
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(request.getEmail())).thenReturn("jwt-token");
        when(userService.getUserByEmail(request.getEmail())).thenReturn(user);

        var response = authController.login(request, servletResponse);
        Map<String, Object> body = response.getBody();
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        ArgumentCaptor<String> cookieCaptor = ArgumentCaptor.forClass(String.class);

        verify(authenticationManager).authenticate(authCaptor.capture());
        verify(servletResponse).addHeader(org.mockito.ArgumentMatchers.eq(HttpHeaders.SET_COOKIE), cookieCaptor.capture());

        assertEquals(request.getEmail(), authCaptor.getValue().getName());
        assertEquals(request.getPassword(), authCaptor.getValue().getCredentials());
        assertNotNull(body);
        assertEquals("User logged in successfully.", body.get("message"));
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Object payload = body.get("data");
        assertInstanceOf(AuthLogin.Response.class, payload);
        AuthLogin.Response loginResponse = (AuthLogin.Response) payload;
        assertEquals("jwt-token", loginResponse.getToken());
        assertSame(user, loginResponse.getUser());
        assertTrue(cookieCaptor.getValue().contains("_TCS_AUTH=jwt-token"));
        assertTrue(cookieCaptor.getValue().contains("HttpOnly"));
        assertTrue(cookieCaptor.getValue().contains("Secure"));
    }

    @Test
    void doesCheckAuthenticationReturnUnauthorizedWithoutCookie()
    {
        var response = authController.checkAuthentication("NOT_AUTH");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(body);
        assertEquals("User is not authorized.", body.get("message"));
    }

    @Test
    void doesCheckAuthenticationReturnUserForValidToken()
    {
        User user = buildUser("john@unit.run");

        when(userService.getUserByToken("jwt-token")).thenReturn(user);

        var responseEntity = authController.checkAuthentication("jwt-token");
        Map<String, Object> body = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(body);
        assertEquals("Authentication valid, logged in as john@unit.run.", body.get("message"));

        Object payload = body.get("data");
        assertInstanceOf(AuthLogin.Response.class, payload);
        AuthLogin.Response response = (AuthLogin.Response) payload;
        assertEquals("jwt-token", response.getToken());
        assertSame(user, response.getUser());
    }

    private User buildUser(String email)
    {
        return User.builder()
                .email(email)
                .name("John")
                .surname("Doe")
                .build();
    }

}
