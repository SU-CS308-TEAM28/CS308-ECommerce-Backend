package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.repositories.PagedProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService
{

    @AllArgsConstructor
    public enum Category {
        COMPUTERS("Computers"),
        TABLETS("Tablets"),
        PHONES("Phones"),
        HOME_AND_LIVING("Home & Living"),
        TVs("TVs");

        private final String name;
    };

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PagedProductRepository pagedProductRepository;

    public Optional<Product> getProduct(String id)
    {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts()
    {
        return productRepository.findAll();
    }

    public Page<Product> getPagedProducts(int page, int size, String sort, String order)
    {
        return pagedProductRepository.findAll(PageRequest.of(
                page,
                size,
                order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
        ));
    }

    public Page<Product> getPagedProducts(int page, int size, String sort, String order, Category category, int minPrice, int maxPrice)
    {
        return pagedProductRepository.findAllByCategoryContainingIgnoreCaseAndPriceIsBetween(
                category.name(),
                minPrice,
                maxPrice,
                PageRequest.of(
                        page,
                        size,
                        order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
                )
        );
    }

    public Page<Product> getPagedProducts(int page, int size, String sort, String order, String searchQuery, int minPrice, int maxPrice)
    {
        return pagedProductRepository.findAllByKeywordAndFilterByPriceRange(
                searchQuery,
                minPrice,
                maxPrice,
                PageRequest.of(
                        page,
                        size,
                        order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
                )
        );
    }

}
