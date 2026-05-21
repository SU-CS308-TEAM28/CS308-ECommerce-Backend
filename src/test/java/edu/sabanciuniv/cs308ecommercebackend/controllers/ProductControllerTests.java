package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Category;
import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetProducts;
import edu.sabanciuniv.cs308ecommercebackend.services.CategoryService;
import edu.sabanciuniv.cs308ecommercebackend.services.CommentService;
import edu.sabanciuniv.cs308ecommercebackend.services.OrderService;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTests
{

    @Mock
    private ProductService productService;

    @Mock
    private CommentService commentService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ProductController productController;

    @Test
    void doesGetProductReturnRequestedProduct()
    {
        Product product = buildProduct("product-1", "Laptop");

        when(productService.getProduct("product-1")).thenReturn(Optional.of(product));

        var response = productController.getProduct("product-1");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Successfully returned product.", body.get("message"));
        assertSame(product, body.get("data"));
    }

    @Test
    void doesGetProductReturnNotFoundWhenProductMissing()
    {
        when(productService.getProduct("missing-product")).thenReturn(Optional.empty());

        var response = productController.getProduct("missing-product");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Product not found.", body.get("message"));
    }

    @Test
    void doesGetAllProductsUseDefaultListingBranch()
    {
        Product product = buildProduct("product-1", "Laptop");
        Page<Product> productsPage = new PageImpl<>(List.of(product), PageRequest.of(0, 5), 8);

        when(productService.getPagedProducts(0, 5, "ratings.value", "desc")).thenReturn(productsPage);
        when(categoryService.getCategory("COMPUTERS")).thenReturn(buildCategory("COMPUTERS", "Computers"));
        when(categoryService.getSubCategory("LAPTOPS")).thenReturn(buildCategory("LAPTOPS", "Laptops"));

        var responseEntity = productController.getAllProducts(0, 5, "ratings.value", "desc", "", "", 0, -1);
        Map<String, Object> body = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(body);
        assertEquals("Successfully retrieved products.", body.get("message"));

        Object payload = body.get("data");
        assertInstanceOf(GetProducts.Response.class, payload);
        GetProducts.Response response = (GetProducts.Response) payload;
        assertEquals(2, response.getPageCount());
        List<Product> responseProducts = response.getProducts().toList();
        assertEquals(List.of(product), responseProducts);
        assertEquals("Computers", responseProducts.get(0).getCategory());
        assertEquals(List.of("Laptops"), responseProducts.get(0).getSubcategories());

        verify(productService).getPagedProducts(0, 5, "ratings.value", "desc");
        verify(productService, never()).getPagedProducts(anyInt(), anyInt(), anyString(), anyString(), any(Category.class), anyInt(), anyInt());
        verify(productService, never()).getPagedProducts(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyInt(), anyInt());
        verify(categoryService).getCategory("COMPUTERS");
        verify(categoryService).getSubCategory("LAPTOPS");
    }

    @Test
    void doesGetAllProductsUseCategoryBranch()
    {
        Product product = buildProduct("product-2", "Tablet");
        Page<Product> productsPage = new PageImpl<>(List.of(product), PageRequest.of(1, 2), 4);
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        when(productService.getPagedProducts(1, 2, "price", "asc", Category.builder().abbrv("COMPUTERS").build(), 10, Integer.MAX_VALUE))
                .thenReturn(productsPage);
        when(categoryService.getCategory("COMPUTERS")).thenReturn(buildCategory("COMPUTERS", "Computers"));
        when(categoryService.getSubCategory("LAPTOPS")).thenReturn(buildCategory("LAPTOPS", "Laptops"));

        var responseEntity = productController.getAllProducts(1, 2, "price", "asc", "COMPUTERS", "", 10, -1);
        Map<String, Object> body = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(body);

        Object payload = body.get("data");
        assertInstanceOf(GetProducts.Response.class, payload);
        GetProducts.Response response = (GetProducts.Response) payload;
        assertEquals(2, response.getPageCount());
        List<Product> responseProducts = response.getProducts().toList();
        assertEquals(List.of(product), responseProducts);
        assertEquals("Computers", responseProducts.get(0).getCategory());
        assertEquals(List.of("Laptops"), responseProducts.get(0).getSubcategories());

        verify(productService).getPagedProducts(org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(2), org.mockito.ArgumentMatchers.eq("price"), org.mockito.ArgumentMatchers.eq("asc"), categoryCaptor.capture(), org.mockito.ArgumentMatchers.eq(10), org.mockito.ArgumentMatchers.eq(Integer.MAX_VALUE));
        assertEquals("COMPUTERS", categoryCaptor.getValue().getAbbrv());
        verify(categoryService).getCategory("COMPUTERS");
        verify(categoryService).getSubCategory("LAPTOPS");
    }

    @Test
    void doesGetAllProductsUseSearchBranch()
    {
        Product product = buildProduct("product-3", "Phone");
        Page<Product> productsPage = new PageImpl<>(List.of(product), PageRequest.of(0, 3), 1);

        when(productService.getPagedProducts(0, 3, "name", "desc", "phone", 100, 500))
                .thenReturn(productsPage);
        when(categoryService.getCategory("COMPUTERS")).thenReturn(buildCategory("COMPUTERS", "Computers"));
        when(categoryService.getSubCategory("LAPTOPS")).thenReturn(buildCategory("LAPTOPS", "Laptops"));

        var responseEntity = productController.getAllProducts(0, 3, "name", "desc", "", "phone", 100, 500);
        Map<String, Object> body = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(body);

        Object payload = body.get("data");
        assertInstanceOf(GetProducts.Response.class, payload);
        GetProducts.Response response = (GetProducts.Response) payload;
        assertEquals(1, response.getPageCount());
        List<Product> responseProducts = response.getProducts().toList();
        assertEquals(List.of(product), responseProducts);
        assertEquals("Computers", responseProducts.get(0).getCategory());
        assertEquals(List.of("Laptops"), responseProducts.get(0).getSubcategories());

        verify(productService).getPagedProducts(0, 3, "name", "desc", "phone", 100, 500);
        verify(categoryService).getCategory("COMPUTERS");
        verify(categoryService).getSubCategory("LAPTOPS");
    }

    private Product buildProduct(String id, String name)
    {
        return Product.builder()
                .id(id)
                .name(name)
                .price(1999.99)
                .category("COMPUTERS")
                .subcategories(List.of("LAPTOPS"))
                .build();
    }

    private Category buildCategory(String abbrv, String label)
    {
        return Category.builder()
                .abbrv(abbrv)
                .label(label)
                .build();
    }

}
