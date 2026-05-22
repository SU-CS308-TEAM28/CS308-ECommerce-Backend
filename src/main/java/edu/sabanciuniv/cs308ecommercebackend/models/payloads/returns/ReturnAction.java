package edu.sabanciuniv.cs308ecommercebackend.models.payloads.returns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReturnAction
{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request
    {
        String returnId;
    }
}
