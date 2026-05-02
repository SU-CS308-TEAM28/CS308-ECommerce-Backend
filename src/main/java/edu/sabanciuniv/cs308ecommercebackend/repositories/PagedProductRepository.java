package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagedProductRepository extends PagingAndSortingRepository<Product, String>
{
    Page<Product> findAllByCategoryContainingIgnoreCaseAndPriceIsBetween(String category, double priceMin, double priceMax, Pageable pageable);
    @Query("{ '$and': [" +
                "{ '$or': [ " +
                    "{ 'name': { '$regex': ?0, '$options': 'i' } }, " +
                    "{ 'description': { '$regex': ?0, '$options': 'i' } } " +
                "]}, " +
                "{ 'price': { '$gte': ?1, '$lte': ?2 } }" +
            "]}")
    Page<Product> findAllByKeywordAndFilterByPriceRange(String keyword, double priceMin, double priceMax, Pageable pageable);
}
