package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.AddToCart;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.ChangeQuantity;
import edu.sabanciuniv.cs308ecommercebackend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/shopping-cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public TeknocsResponse<?> getCart(@CookieValue(name = "_TCS_AUTH") String token) {
        try {
            return new TeknocsResponse<>(HttpStatus.OK, "Cart retrieved", cartService.getCart(token));
        } catch (Exception e) {
            return new TeknocsResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/add")
    public TeknocsResponse<?> addToCart(
            @CookieValue(name = "_TCS_AUTH") String token,
            @RequestBody AddToCart request) {
        try {
            return new TeknocsResponse<>(HttpStatus.OK, "Item added to cart", cartService.addToCart(token, request.getProductId(), request.getQuantity()));
        } catch (Exception e) {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PostMapping("/increase-quantity")
    public TeknocsResponse<?> increaseQuantity(
            @CookieValue(name = "_TCS_AUTH") String token,
            @RequestBody ChangeQuantity request) {
        if (request.getAmount() == null || request.getAmount() <= 0)
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, "Amount must be greater than zero", null);
        try {
            return new TeknocsResponse<>(HttpStatus.OK, "Quantity increased", cartService.increaseQuantity(token, request.getProductId(), request.getAmount()));
        } catch (Exception e) {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PostMapping("/decrease-quantity")
    public TeknocsResponse<?> decreaseQuantity(
            @CookieValue(name = "_TCS_AUTH") String token,
            @RequestBody ChangeQuantity request) {
        if (request.getAmount() == null || request.getAmount() <= 0)
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, "Amount must be greater than zero", null);
        try {
            return new TeknocsResponse<>(HttpStatus.OK, "Quantity decreased", cartService.decreaseQuantity(token, request.getProductId(), request.getAmount()));
        } catch (Exception e) {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @DeleteMapping("/remove/{id}")
    public TeknocsResponse<?> removeFromCart(
            @CookieValue(name = "_TCS_AUTH") String token,
            @PathVariable String id) {
        try {
            return new TeknocsResponse<>(HttpStatus.OK, "Item removed from cart", cartService.removeFromCart(token, id));
        } catch (Exception e) {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }
}
