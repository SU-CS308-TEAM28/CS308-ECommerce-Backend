package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Comment;
import edu.sabanciuniv.cs308ecommercebackend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

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

    public Page<Comment> getPagedComments (String productId, int page)
    {
        return pagedCommentRepository.findAllByProductId(productId, PageRequest.of(
                page,
                5,
                Sort.by(Sort.Direction.DESC, "creationDate")
        ));
    }

    public Comment postComment (String productId, String userId, String publicName, int rate, String comment)
    {
        // TODO Reflect rate on product (with the power of Math! not by averaging every rate!)

        return commentRepository.save(
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
    }

}
