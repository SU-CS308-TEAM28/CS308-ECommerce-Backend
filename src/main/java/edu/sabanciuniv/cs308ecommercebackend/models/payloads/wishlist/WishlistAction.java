package edu.sabanciuniv.cs308ecommercebackend.models.payloads.wishlist;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import lombok.*;

import java.util.Date;

public class WishlistAction
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
    public static class WishlistProduct
    {
        public String productId;
        public String name;
        public String thumbnailUrl;
        public double price;
        public double activeDiscount;
        public int stock;
        public Product.Ratings rating;
        public Date dateAdded;
    }
}
