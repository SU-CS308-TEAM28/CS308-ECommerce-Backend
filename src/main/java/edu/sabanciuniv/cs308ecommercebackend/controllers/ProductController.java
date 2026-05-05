package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Comment;
import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.AddComment;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetComments;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.GetProducts;
import edu.sabanciuniv.cs308ecommercebackend.services.CommentService;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    private UserService userService;

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
            productsPage = productService.getPagedProducts(page, size, sort, order, ProductService.Category.valueOf(category), minPrice, maxPrice != -1 ? maxPrice : Integer.MAX_VALUE);
        else if (!search.isBlank())
            productsPage = productService.getPagedProducts(page, size, sort, order, search, minPrice, maxPrice != -1 ? maxPrice : Integer.MAX_VALUE);
        else
            productsPage = productService.getPagedProducts(page, size, sort, order);

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Successfully retrieved products.",
                GetProducts.Response.builder()
                        .pageCount(productsPage.getTotalPages())
                        .products(productsPage.get())
                        .build()
        );
    }

    @GetMapping("/product/{id}/comments")
    public TeknocsResponse<GetComments.Response> getComments(@PathVariable String id, @RequestParam(defaultValue = "0") int page)
    {
        Page<Comment> commentsPage = commentService.getPagedComments(id, page);

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Successfully retrieved products.",
                GetComments.Response.builder()
                        .pageCount(commentsPage.getTotalPages())
                        .comments(commentsPage.get())
                        .build()
        );
    }

    @PostMapping("/product/{id}/comments/add")
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

        Comment comment = commentService.postComment(
                id,
                user.getId(),
                request.isNameShown() ?
                        user.getName() + " " + user.getSurname() :
                        user.getName().charAt(0) + "*** " + user.getSurname().charAt(0) + "***",
                request.getRate(),
                request.getComment()
        );

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


}
