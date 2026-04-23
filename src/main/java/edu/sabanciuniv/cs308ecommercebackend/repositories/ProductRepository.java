package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>
{
    Product findById(int id);
    Product findByName(String name);
}
