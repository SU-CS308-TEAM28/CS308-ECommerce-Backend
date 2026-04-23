package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetProducts;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController
{

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public TeknocsResponse<GetProducts.Response> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ratings.value") String sort,
            @RequestParam(defaultValue = "desc") String order)
    {
        Page<Product> p = productService.getPagedProducts(page, size, sort, order);

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Successfully retrieved products.",
                GetProducts.Response.builder()
                        .pageCount(p.getTotalPages())
                        .products(p.get())
                        .build()
        );
    }

}
