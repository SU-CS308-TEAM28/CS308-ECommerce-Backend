package edu.sabanciuniv.cs308ecommercebackend.models.payloads.product;

import lombok.AllArgsConstructor;
import lombok.Data;

public class CommentAction
{
    @Data
    @AllArgsConstructor
    public static class Request
    {
        public String commentId;
    }
}
