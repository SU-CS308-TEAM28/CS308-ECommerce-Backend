package edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import lombok.*;

import java.util.Set;

public class CartAction
{
    @Data
    @AllArgsConstructor
    public static class Request
    {
        public String productId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartProduct
    {
        public String productId;
        public String name;
        public int quantity;
        public String thumbnailUrl;
        public double price;
        public double activeDiscount;
        public int stock;
        public Product.Ratings rating;
    }
}
