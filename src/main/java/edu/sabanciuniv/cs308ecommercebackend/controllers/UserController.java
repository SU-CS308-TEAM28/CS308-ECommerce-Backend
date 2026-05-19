package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.user.UserFieldChange;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController
{

    @Autowired
    private UserService userService;

    @PostMapping("/change-address")
    public TeknocsResponse<User> changeAddress(@CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token, @RequestBody UserFieldChange.Request request) throws BadRequestException
    {
        // TODO Ground Checks
        if (token.equals("NOT_AUTH"))
            throw new BadRequestException("Impossible state");

        User user = userService.changeUserHomeAddress(token, request.getNewValue());

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "User home address updated successfully.",
                user
        );
    }

    @PostMapping("/change-tax")
    TeknocsResponse<User> changeTaxId(@CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token, @RequestBody UserFieldChange.Request request) throws BadRequestException
    {
        // TODO Ground Checks
        if (token.equals("NOT_AUTH"))
            throw new BadRequestException("Impossible state");

        User user = userService.changeUserTaxId(token, request.getNewValue());

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "User tax ID updated successfully.",
                user
        );
    }

}
