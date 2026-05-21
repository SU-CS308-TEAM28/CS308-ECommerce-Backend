package edu.sabanciuniv.cs308ecommercebackend.models.payloads.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class BulkDiscount
{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request
    {
        List<String> productIds;
        double discount;
    }
}
