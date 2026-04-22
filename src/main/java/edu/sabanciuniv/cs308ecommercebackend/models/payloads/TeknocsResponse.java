package edu.sabanciuniv.cs308ecommercebackend.models.payloads;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

public class TeknocsResponse<T> extends ResponseEntity<Map<String, Object>>
{
    public TeknocsResponse(HttpStatus status, String payloadMessage, @Nullable T payloadData)
    {
        super(Map.of(
                "message", payloadMessage,
                "data", Objects.requireNonNullElse(payloadData, new Object())
        ), status);
    }
}