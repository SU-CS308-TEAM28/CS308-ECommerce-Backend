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

@Service
public class ProductService
{

    @AllArgsConstructor
    public enum Category {
        COMPUTERS("Computers"),
        TABLETS("Tablets"),
        PHONES("Phones"),
        HOME("Home");

        private final String name;
    };

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PagedProductRepository pagedProductRepository;

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

    public Page<Product> getPagedProducts(int page, int size, String sort, String order, Category category)
    {
        return pagedProductRepository.findAllByCategoryContainingIgnoreCase(
                category.name(),
                PageRequest.of(
                    page,
                    size,
                    order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
                )
        );
    }

    public Page<Product> getPagedProducts(int page, int size, String sort, String order, String searchQuery)
    {
        return pagedProductRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchQuery,
                searchQuery,
                PageRequest.of(
                    page,
                    size,
                    order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
                )
        );
    }

}
