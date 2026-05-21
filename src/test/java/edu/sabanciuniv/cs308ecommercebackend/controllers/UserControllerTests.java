package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.user.UserFieldChange;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests
{

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void doesChangeAddressReturnUpdatedUser() throws Exception
    {
        UserFieldChange.Request request = new UserFieldChange.Request("Istanbul Address");
        User user = buildUser();
        user.setHomeAddress("Istanbul Address");

        when(userService.changeUserHomeAddress("jwt-token", "Istanbul Address")).thenReturn(user);

        var response = userController.changeAddress("jwt-token", request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("User home address updated successfully.", body.get("message"));
        assertSame(user, body.get("data"));

        verify(userService).changeUserHomeAddress("jwt-token", "Istanbul Address");
    }

    @Test
    void doesChangeAddressThrowBadRequestForNotAuthToken()
    {
        UserFieldChange.Request request = new UserFieldChange.Request("Istanbul Address");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userController.changeAddress("NOT_AUTH", request));

        assertEquals("Impossible state", exception.getMessage());
    }

    @Test
    void doesChangeTaxIdReturnUpdatedUser() throws Exception
    {
        UserFieldChange.Request request = new UserFieldChange.Request("12345678901");
        User user = buildUser();
        user.setTaxId("12345678901");

        when(userService.changeUserTaxId("jwt-token", "12345678901")).thenReturn(user);

        var response = userController.changeTaxId("jwt-token", request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("User tax ID updated successfully.", body.get("message"));
        assertSame(user, body.get("data"));

        verify(userService).changeUserTaxId("jwt-token", "12345678901");
    }

    @Test
    void doesChangeTaxIdThrowBadRequestForNotAuthToken()
    {
        UserFieldChange.Request request = new UserFieldChange.Request("12345678901");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userController.changeTaxId("NOT_AUTH", request));

        assertEquals("Impossible state", exception.getMessage());
    }

    private User buildUser()
    {
        return User.builder()
                .id("user-1")
                .email("john@unit.run")
                .name("John")
                .surname("Doe")
                .build();
    }

}
