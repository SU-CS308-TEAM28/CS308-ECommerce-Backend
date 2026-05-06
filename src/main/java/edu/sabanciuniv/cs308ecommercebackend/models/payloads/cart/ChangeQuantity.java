package edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart;

import lombok.Data;

@Data
public class ChangeQuantity {
    String productId;
    Integer amount;
}
