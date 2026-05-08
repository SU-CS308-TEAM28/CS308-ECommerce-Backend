package edu.sabanciuniv.cs308ecommercebackend.models.payloads.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PlaceOrder
{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request
    {
        public String cardHolder;
        public String cardNumber;
        public String cardCVV;
        public String cardDueDate;
    }
}