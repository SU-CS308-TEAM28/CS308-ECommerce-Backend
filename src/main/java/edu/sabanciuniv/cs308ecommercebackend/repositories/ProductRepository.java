package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>
{
    Optional<Product> findById(String id);
    Product findByName(String name);
}
