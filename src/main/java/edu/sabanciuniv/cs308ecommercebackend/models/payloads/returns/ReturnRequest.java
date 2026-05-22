package edu.sabanciuniv.cs308ecommercebackend.models.payloads.returns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ReturnRequest
{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request
    {
        String orderId;
        List<ProductReturn> returningProducts;
        String reason;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProductReturn
        {
            String productId;
            Integer quantity;
        }
    }
}
