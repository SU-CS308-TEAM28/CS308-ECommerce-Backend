package edu.sabanciuniv.cs308ecommercebackend.models.payloads.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class AddComment
{
    @Data
    @AllArgsConstructor
    public static class Request
    {
        public int rate;
        public String comment;
        public boolean isNameShown;
    }

    public static class Response {}
}
