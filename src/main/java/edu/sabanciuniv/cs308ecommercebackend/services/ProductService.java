package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.repositories.PagedProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService
{

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
        return pagedProductRepository.findAll(PageRequest.of(page, size, order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()));
    }

}
