package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.repositories.PagedProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/product")
public class ProductController
{

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public PagedProductRepository pagedProductRepository;

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ratings.value") String sort,
            @RequestParam(defaultValue = "desc") String order)
    {
        Page<Product> p =  pagedProductRepository.findAll(PageRequest.of(page, size, order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()));

        return ResponseEntity.ok().body(
                Map.of(
                        "status", 200,
                        "message", "Successfully retrieved products.",
                        "data", Map.of(
                                        "pageCount", p.getTotalPages(),
                                        "products", p.get()
                                    )
                )
        );
    }

}
