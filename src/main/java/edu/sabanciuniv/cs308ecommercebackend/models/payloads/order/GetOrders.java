package edu.sabanciuniv.cs308ecommercebackend.models.payloads.order;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

public class GetOrders
{
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderData
    {
        String id;
        String userId;
        Date orderDate;
        List<CartAction.CartProduct> products;
        String status;
        double totalPrice;
        String deliveryAddress;
        boolean isCompleted;
        boolean isCancelled;
    }
}
