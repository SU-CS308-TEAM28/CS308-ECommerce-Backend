package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    public ProductRepository productRepository;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test () {
        return ResponseEntity.ok(
                Map.of(
                        "message", "Test endpoint success.",
                        "currentProductCount", productRepository.findAll(Pageable.ofSize(50)).stream().count() ,
                        "timestamp", String.valueOf(System.currentTimeMillis())
                )
        );
    }
}
