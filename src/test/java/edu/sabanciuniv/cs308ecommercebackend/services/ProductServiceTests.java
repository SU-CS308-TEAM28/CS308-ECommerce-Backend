package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.repositories.PagedProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests
{

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PagedProductRepository pagedProductRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void doesGetProductReturnRepositoryResult()
    {
        Product product = buildProduct("product-1", "Laptop");

        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProduct("product-1");

        assertTrue(result.isPresent());
        assertSame(product, result.get());
        verify(productRepository).findById("product-1");
    }

    @Test
    void doesGetAllProductsReturnRepositoryList()
    {
        List<Product> products = List.of(
                buildProduct("product-1", "Laptop"),
                buildProduct("product-2", "Tablet")
        );

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(products, result);
        verify(productRepository).findAll();
    }

    @Test
    void doesGetPagedProductsUseDescendingSortForDefaultListing()
    {
        Page<Product> expectedPage = new PageImpl<>(List.of(buildProduct("product-1", "Laptop")));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(pagedProductRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<Product> result = productService.getPagedProducts(2, 4, "ratings.value", "desc");

        verify(pagedProductRepository).findAll(pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertSame(expectedPage, result);
        assertEquals(2, pageable.getPageNumber());
        assertEquals(4, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("ratings.value").getDirection());
    }

    @Test
    void doesGetPagedProductsUseCategoryFilterAndAscendingSort()
    {
        Page<Product> expectedPage = new PageImpl<>(List.of(buildProduct("product-2", "Tablet")));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(pagedProductRepository.findAllByCategoryContainingIgnoreCaseAndPriceIsBetween(
                any(),
                anyDouble(),
                anyDouble(),
                any(Pageable.class)
        )).thenReturn(expectedPage);

        Page<Product> result = productService.getPagedProducts(1, 3, "price", "asc", ProductService.Category.TABLETS, 100, 500);

        verify(pagedProductRepository).findAllByCategoryContainingIgnoreCaseAndPriceIsBetween(
                org.mockito.ArgumentMatchers.eq("TABLETS"),
                org.mockito.ArgumentMatchers.eq(100.0),
                org.mockito.ArgumentMatchers.eq(500.0),
                pageableCaptor.capture()
        );
        Pageable pageable = pageableCaptor.getValue();

        assertSame(expectedPage, result);
        assertEquals(1, pageable.getPageNumber());
        assertEquals(3, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("price").getDirection());
    }

    @Test
    void doesGetPagedProductsUseSearchFilterAndDescendingSort()
    {
        Page<Product> expectedPage = new PageImpl<>(List.of(buildProduct("product-3", "Phone")));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(pagedProductRepository.findAllByKeywordAndFilterByPriceRange(
                any(),
                anyDouble(),
                anyDouble(),
                any(Pageable.class)
        )).thenReturn(expectedPage);

        Page<Product> result = productService.getPagedProducts(0, 6, "name", "desc", "phone", 250, 1200);

        verify(pagedProductRepository).findAllByKeywordAndFilterByPriceRange(
                org.mockito.ArgumentMatchers.eq("phone"),
                org.mockito.ArgumentMatchers.eq(250.0),
                org.mockito.ArgumentMatchers.eq(1200.0),
                pageableCaptor.capture()
        );
        Pageable pageable = pageableCaptor.getValue();

        assertSame(expectedPage, result);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(6, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("name").getDirection());
    }

    private Product buildProduct(String id, String name)
    {
        return Product.builder()
                .id(id)
                .name(name)
                .price(999.99)
                .category("COMPUTERS")
                .build();
    }

}
