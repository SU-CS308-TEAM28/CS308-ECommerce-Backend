package edu.sabanciuniv.cs308ecommercebackend.models.payloads.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UpdateOrder
{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request
    {
        public String status;
    }
}
