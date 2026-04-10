package edu.sabanciuniv.cs308ecommercebackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Products")
public class Product {
    @Id
    String id;
    String name;
    double price;
    String image;
    String category;
    String subcategory;
    String description;
}
