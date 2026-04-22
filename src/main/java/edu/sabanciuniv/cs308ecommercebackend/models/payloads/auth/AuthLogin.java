package edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class AuthLogin
{
    @Data
    @AllArgsConstructor
    public static class Request
    {
        public String email;
        public String password;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response
    {
        private String token;
        private User user;
    }
}
