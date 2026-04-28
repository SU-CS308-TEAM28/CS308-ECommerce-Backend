package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetProducts;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    public void getAllProducts_ShouldReturnProducts() {
        Product product = Product.builder().id("1").name("Test Product").build();
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 5), 1);

        when(productService.getPagedProducts(anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        var response = productController.getAllProducts(0, 5, "ratings.value", "desc", "", "");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("Successfully retrieved products.");
        assertThat(((GetProducts.Response) response.getBody().get("data")).getPageCount()).isEqualTo(1);
    }

    @Test
    public void getAllProducts_WithCategory_ShouldReturnFilteredProducts() {
        Product product = Product.builder().id("1").name("Test Product").build();
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 5), 1);

        when(productService.getPagedProducts(anyInt(), anyInt(), anyString(), anyString(), any(ProductService.Category.class))).thenReturn(page);

        var response = productController.getAllProducts(0, 5, "ratings.value", "desc", "COMPUTERS", "");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("Successfully retrieved products.");
    }

    @Test
    public void getAllProducts_WithSearch_ShouldReturnSearchedProducts() {
        Product product = Product.builder().id("1").name("Test Product").build();
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 5), 1);

        when(productService.getPagedProducts(anyInt(), anyInt(), anyString(), anyString(), anyString())).thenReturn(page);

        var response = productController.getAllProducts(0, 5, "ratings.value", "desc", "", "test");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("Successfully retrieved products.");
    }

    @Test
    public void getAllProducts_WithParams_ShouldUseParams() {
        Product product = Product.builder().id("1").name("Test Product").build();
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(1, 10), 1);

        when(productService.getPagedProducts(eq(1), eq(10), eq("price"), eq("asc"))).thenReturn(page);

        var response = productController.getAllProducts(1, 10, "price", "asc", "", "");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
