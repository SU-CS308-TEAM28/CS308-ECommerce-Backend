package edu.sabanciuniv.cs308ecommercebackend.models.payloads.user;

import lombok.AllArgsConstructor;
import lombok.Data;

public class UserFieldChange
{
    @Data
    @AllArgsConstructor
    public static class Request
    {
        public String newValue;
    }
}
