package edu.sabanciuniv.cs308ecommercebackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/test")
    public static ResponseEntity<Map<String, String>> test () {
        return ResponseEntity.ok(Map.of("message", "Test endpoint success.", "timestamp", String.valueOf(System.currentTimeMillis())));
    }
}
