package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>
{
    List<Order> findAllByUserId(String userId);
    List<Order> findAllByIsCompletedFalse();
    List<Order> findAllByUserIdAndOrderDateBetween(String userId, Date start, Date end);
    List<Order> findAllByIsCompletedFalseAndOrderDateBetween(Date start, Date end);
    List<Order> findAllByOrderDateBetween(Date start, Date end);
    Order findByIdAndUserId(String id, String userId);
    Order findByProductsProductIdAndUserId(String productId, String userId);
}
