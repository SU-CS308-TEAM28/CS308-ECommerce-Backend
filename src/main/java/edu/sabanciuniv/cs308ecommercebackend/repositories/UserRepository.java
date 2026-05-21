package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String>
{
    User findByEmail(String email); // TODO Might be a good idea to go for type Optional

    @Query("{ 'userData.wishlist.productId': { $in: ?0 } }")
    List<User> findAllByWishlistProductIdIn(List<String> productIds);
}
