package edu.sabanciuniv.cs308ecommercebackend.models.payloads.product;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductAction
{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request
    {
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
        Integer stock;
        List<Product.ExtraProperty> extraProps;
    }
}
