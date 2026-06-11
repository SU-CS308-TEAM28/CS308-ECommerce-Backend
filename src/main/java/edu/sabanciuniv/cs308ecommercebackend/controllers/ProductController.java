package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.*;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.AddComment;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.BulkDiscount;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.CommentAction;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetComments;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetProducts;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.ProductAction;
import edu.sabanciuniv.cs308ecommercebackend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
public class ProductController
{

    @Autowired
    private ProductService productService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public TeknocsResponse<Product> addProduct(@RequestBody ProductAction.Request request)
    {
        try
        {
            return new TeknocsResponse<>(HttpStatus.CREATED, "Product added successfully.", productService.addProduct(request));
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public TeknocsResponse<Product> updateProduct(
            @PathVariable String id,
            @RequestBody ProductAction.Request request,
            @CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token
    )
    {
        if (userService.getUserByToken(token).getUserType().equals("sales_manager"))
        {
            try
            {
                return new TeknocsResponse<>(HttpStatus.OK, "Product price updated successfully.", productService.updateProductPrice(id, request.getPrice()));
            }
            catch (Exception e)
            {
                return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            }
        }

        try
        {
            return new TeknocsResponse<>(HttpStatus.OK, "Product updated successfully.", productService.updateProduct(id, request));
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public TeknocsResponse<?> deleteProduct(@PathVariable String id)
    {
        try
        {
            productService.removeProduct(id);
            return new TeknocsResponse<>(HttpStatus.OK, "Product removed successfully.", null);
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @GetMapping("/{id}")
    public TeknocsResponse<Product> getProduct(@PathVariable String id)
    {
        Optional<Product> product = productService.getProduct(id);

        return product
                .map(value -> new TeknocsResponse<>(HttpStatus.OK, "Successfully returned product.", value))
                .orElseGet(() -> new TeknocsResponse<>(HttpStatus.NOT_FOUND, "Product not found.", null));
    }

    @GetMapping("/products")
    public TeknocsResponse<GetProducts.Response> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ratings.value") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(defaultValue = "-1") int maxPrice)
    {
        Page<Product> productsPage;
        if (!category.isBlank())
            productsPage = productService.getPagedProducts(page, size, sort, order, Category.builder().abbrv(category).build(), minPrice, maxPrice != -1 ? maxPrice : Integer.MAX_VALUE);
        else if (!search.isBlank())
            productsPage = productService.getPagedProducts(page, size, sort, order, search, minPrice, maxPrice != -1 ? maxPrice : Integer.MAX_VALUE);
        else
            productsPage = productService.getPagedProducts(page, size, sort, order);

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Successfully retrieved products.",
                GetProducts.Response.builder()
                        .pageCount(productsPage.getTotalPages())
                        .products(productsPage.stream().peek(product -> {
                            product.setCategory(categoryService.getCategory(product.getCategory()).getLabel());
                            product.setSubcategories(product.getSubcategories().stream().map(subcategory -> categoryService.getSubCategory(subcategory).getLabel()).toList());
                        }))
                        .build()
        );
    }

    @PutMapping("/bulk-discount")
    public TeknocsResponse<List<Product>> bulkDiscount(@RequestBody BulkDiscount.Request request)
    {
        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Discount applied successfully.",
                productService.bulkDiscount(request.getProductIds(), request.getDiscount())
        );
    }

    @GetMapping("/{id}/comments")
    public TeknocsResponse<GetComments.Response> getComments(@PathVariable String id, @RequestParam(defaultValue = "0") int page)
    {
        Page<Comment> commentsPage = commentService.getPagedComments(id, page);

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Successfully retrieved products.",
                GetComments.Response.builder()
                        .pageCount(commentsPage.getTotalPages())
                        .comments(commentsPage.map(comment -> {
                            if (!comment.isApproved())
                                comment.setComment("");

                            return comment;
                        }).get())
                        .build()
        );
    }

    @PostMapping("/{id}/comments/add")
    public TeknocsResponse<AddComment.Response> addComment(
            @RequestBody AddComment.Request request,
            @PathVariable String id,
            @CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token)
    {
        User user = userService.getUserByToken(token);

        if (commentService.getUserComment(user.getId(), id).isPresent())
            return new TeknocsResponse<>(
                    HttpStatus.FORBIDDEN,
                    "User reviews are only allowed once per user.",
                    null
            );

        Order orderOfUser = orderService.getOrderOfUser(id, user);
        if (orderOfUser == null || !orderOfUser.isCompleted())
            return new TeknocsResponse<>(
                    HttpStatus.FORBIDDEN,
                    "In order to be able review a product, you must've purchased and received it.",
                    null
            );

        Comment comment = null;
        try
        {
            comment = commentService.postComment(
                    id,
                    user.getId(),
                    request.isNameShown() ?
                            user.getName() + " " + user.getSurname() :
                            user.getName().charAt(0) + "*** " + user.getSurname().charAt(0) + "***",
                    request.getRate(),
                    request.getComment()
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "The product is updated partially! Notify an admin immediately. This shouldn't have happened.",
                    null
            );
        }

        if (comment != null)
            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Comment posted successfully, waiting for approval...",
                    null
            );
        else
            return new TeknocsResponse<>(
                    HttpStatus.BAD_REQUEST,
                    "Comment was unable to be posted. Unexpected error.",
                    null
            );
    }

    @GetMapping("/awaiting-comments")
    public TeknocsResponse<List<Comment>> getAwaitingComments()
    {
        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Successfully retrieved awaiting comments.",
                commentService.getAwaitingComments()
        );
    }

    @PutMapping("/{id}/comments/approve")
    public TeknocsResponse<Comment> approveComment(@PathVariable String id, @RequestBody CommentAction.Request request)
    {
        try
        {
            return new TeknocsResponse<>(HttpStatus.OK, "Comment approved.", commentService.approveComment(request.getCommentId()));
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.NOT_FOUND, "Comment not found.", null);
        }
    }

    @PutMapping("/{id}/comments/disapprove")
    public TeknocsResponse<Comment> disapproveComment(@PathVariable String id, @RequestBody CommentAction.Request request)
    {
        try
        {
            return new TeknocsResponse<>(HttpStatus.OK, "Comment disapproved.", commentService.disapproveComment(request.getCommentId()));
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.NOT_FOUND, "Comment not found.", null);
        }
    }


}
