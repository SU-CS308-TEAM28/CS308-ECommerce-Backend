package edu.sabanciuniv.cs308ecommercebackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Products")
public class Product
{
    // TODO Restructure product model to conform with the DB structure updates.
    @Id
    String id;
    String name;
    double price;
    String image;
    String category;
    List<String> subcategories;
    String description;
    Ratings ratings;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ratings
    {
        Integer count;
        Double value;
    }
}
