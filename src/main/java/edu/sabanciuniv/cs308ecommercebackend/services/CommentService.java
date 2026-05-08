package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Comment;
import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.repositories.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class CommentService
{

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PagedProductRepository pagedProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PagedCommentRepository pagedCommentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<Comment> getPagedComments (String productId, int page)
    {
        return pagedCommentRepository.findAllByProductId(productId, PageRequest.of(
                page,
                5,
                Sort.by(Sort.Direction.DESC, "creationDate")
        ));
    }

    public Optional<Comment> getUserComment (String userId, String productId)
    {
        return commentRepository.findAllByProductId(productId).filter(comment -> comment.getCommenter().getId().equals(userId)).findFirst();
    }

    public Comment postComment (String productId, String userId, String publicName, int rate, String comment) throws Exception
    {
        Comment savedComment = commentRepository.save(
                Comment.builder()
                        .productId(productId)
                        .commenter(
                                Comment.Commenter.builder()
                                        .id(userId)
                                        .publicName(publicName)
                                        .build()
                        )
                        .creationDate(Date.from(Instant.now()))
                        .rate(rate)
                        .comment(comment)
                        .isApproved(false)
                        .build()
        );

        Document productRatingsQuery = mongoTemplate.aggregate(
                newAggregation(
                        match(Criteria.where("productId").is(productId)),
                        group()
                                .avg("rate").as("averageRate")
                                .count().as("rateCount")
                ),
                "Comments",
                Document.class
        ).getUniqueMappedResult();

        if (productRatingsQuery == null || productRatingsQuery.getDouble("averageRate") == null || productRatingsQuery.getInteger("rateCount") == null)
            throw new Exception("PARTIAL PRODUCT UPDATE WARNING!");

        Product product = productRepository.findById(productId).orElseThrow();
        product.setRatings(Product.Ratings.builder()
                .value(productRatingsQuery.getDouble("averageRate"))
                .count(productRatingsQuery.getInteger("rateCount"))
                .build()
        );
        productRepository.save(product);

        return savedComment;
    }

}
