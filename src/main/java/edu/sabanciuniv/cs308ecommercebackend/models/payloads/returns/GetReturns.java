package edu.sabanciuniv.cs308ecommercebackend.models.payloads.returns;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

public class GetReturns
{
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReturnData
    {
        String id;
        String orderId;
        List<CartAction.CartProduct> products;
        String reason;
        Date requestDate;
        boolean isApproved;
        boolean isCompleted;
    }
}
