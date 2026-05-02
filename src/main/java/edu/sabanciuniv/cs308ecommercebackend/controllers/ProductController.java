package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetProducts;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/product")
public class ProductController
{

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public TeknocsResponse<Product> getProduct(@PathVariable String id)
    {
        Optional<Product> product = productService.getProduct(id);

        return new TeknocsResponse<>(
                product.isPresent() ? HttpStatus.OK : HttpStatus.BAD_REQUEST,
                product.isPresent() ? "Successfully returned product." : "Product does not exist.",
                product.orElse(null)
        );
    }

    @GetMapping("/products")
    public TeknocsResponse<GetProducts.Response> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ratings.value") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(defaultValue = "-1") int maxPrice)
    {
        Page<Product> productsPage;
        if (!category.isBlank())
            productsPage = productService.getPagedProducts(page, size, sort, order, ProductService.Category.valueOf(category), minPrice, maxPrice != -1 ? maxPrice : Integer.MAX_VALUE);
        else if (!search.isBlank())
            productsPage = productService.getPagedProducts(page, size, sort, order, search, minPrice, maxPrice != -1 ? maxPrice : Integer.MAX_VALUE);
        else
            productsPage = productService.getPagedProducts(page, size, sort, order);

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Successfully retrieved products.",
                GetProducts.Response.builder()
                        .pageCount(productsPage.getTotalPages())
                        .products(productsPage.get())
                        .build()
        );
    }

}
