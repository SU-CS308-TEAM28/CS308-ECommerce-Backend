package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController
{

    @GetMapping("/test")
    public TeknocsResponse<Map<String, Object>> test ()
    {
        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Test endpoint request successful.",
                Map.of("timestamp", String.valueOf(System.currentTimeMillis()))
        );
    }

}
