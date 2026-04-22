package edu.sabanciuniv.cs308ecommercebackend.models.payloads.product;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.stream.Stream;

public class GetProducts
{
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response
    {
        private Integer pageCount;
        private Stream<Product> products;
    }
}
