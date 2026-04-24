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
    @Id
    String id;
    String name;
    String description;
    double price;
    double activeDiscount;
    String model;
    String serialNumber;
    String warrantyStatus;
    String distributorInformation;
    String thumbnailUrl;
    List<String> imageUrls;
    String category;
    List<String> subcategories;
    Ratings ratings;
    List<ExtraProperty> extraProps;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ratings
    {
        Integer count;
        Double value;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExtraProperty
    {
        String label;
        String value;
    }
}
