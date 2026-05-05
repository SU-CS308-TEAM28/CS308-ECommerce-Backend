package edu.sabanciuniv.cs308ecommercebackend.models.payloads.product;

import edu.sabanciuniv.cs308ecommercebackend.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.stream.Stream;

public class GetComments
{
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response
    {
        private Integer pageCount;
        private Stream<Comment> comments;
    }
}
