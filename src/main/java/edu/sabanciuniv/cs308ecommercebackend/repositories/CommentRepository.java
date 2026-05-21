package edu.sabanciuniv.cs308ecommercebackend.repositories;

import edu.sabanciuniv.cs308ecommercebackend.models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String>
{
    Stream<Comment> findAllByProductId(String productId);
    List<Comment> findAllByIsChecked(boolean isChecked);
}
