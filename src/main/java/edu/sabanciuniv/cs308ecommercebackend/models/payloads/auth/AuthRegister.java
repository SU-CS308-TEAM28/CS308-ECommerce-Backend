package edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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

    public static class Response {}
}
