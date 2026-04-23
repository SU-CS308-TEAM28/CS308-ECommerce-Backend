package edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

public class AuthRegister
{
    @Data
    @AllArgsConstructor
    public static class Request
    {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Date birthDate;
    }

    // A request class may be omitted if not required but a response class HAS TO EXIST.
    public static class Response {}
}
