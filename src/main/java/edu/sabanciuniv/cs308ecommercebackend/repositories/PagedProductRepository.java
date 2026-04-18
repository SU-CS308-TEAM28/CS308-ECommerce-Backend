package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagedProductRepository extends PagingAndSortingRepository<Product, String> {
}
