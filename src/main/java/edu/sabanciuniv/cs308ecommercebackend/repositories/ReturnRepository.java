package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.Returns;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends MongoRepository<Returns, String>
{
    List<Returns> findAllByOrderIdIn(List<String> orderIds);
    List<Returns> findByOrderId(String orderId);
}
