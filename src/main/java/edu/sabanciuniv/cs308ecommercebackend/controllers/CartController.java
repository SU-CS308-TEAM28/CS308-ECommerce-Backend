package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.services.CartService;
import edu.sabanciuniv.cs308ecommercebackend.utils.CartUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static edu.sabanciuniv.cs308ecommercebackend.utils.CartUtils.*;

@RestController
@RequestMapping("/api/user/shopping-cart")
public class CartController
{

    @Autowired
    private CartUtils cartUtils;

    @Autowired
    private CartService cartService;

    @GetMapping
    public TeknocsResponse<List<CartAction.CartProduct>> getCart(
            @CookieValue(name = AUTH_COOKIE, required = false) String token,
            @CookieValue(name = CART_COOKIE, required = false, defaultValue = EMPTY_CART) String cartCookie,
            HttpServletResponse response)
    {
        try
        {
            if (cartUtils.isAuthenticated(token))
            {
                Set<User.ShoppingCartData> guestCart = cartUtils.parseCartCookie(cartCookie);

                if (!guestCart.isEmpty())
                {
                    Set<User.ShoppingCartData> merged = cartService.replaceCart(token, guestCart);
                    cartUtils.clearCartCookie(response);

                    return new TeknocsResponse<>(HttpStatus.OK, "Cart retrieved", cartService.getCartProductsFromCartMeta(merged));
                }

                return new TeknocsResponse<>(
                        HttpStatus.OK,
                        "Cart retrieved",
                        cartService.getCartProductsFromCartMeta(cartService.getCart(token))
                );
            }

            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Cart retrieved",
                    cartService.getCartProductsFromCartMeta(cartService.getGuestCart(cartUtils.parseCartCookie(cartCookie)))
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/add")
    public TeknocsResponse<List<CartAction.CartProduct>> addToCart(
            @CookieValue(name = AUTH_COOKIE, required = false) String token,
            @CookieValue(name = CART_COOKIE, required = false, defaultValue = EMPTY_CART) String cartCookie,
            @RequestBody CartAction.Request request,
            HttpServletResponse response)
    {
        try
        {
            if (cartUtils.isAuthenticated(token))
                return new TeknocsResponse<>(
                        HttpStatus.OK,
                        "Item added to cart",
                        cartService.getCartProductsFromCartMeta(cartService.addToCart(token, request.getProductId(), 1))
                );

            Set<User.ShoppingCartData> cart = cartService.addToGuestCart(cartUtils.parseCartCookie(cartCookie), request.getProductId(), 1);
            cartUtils.persistCartCookie(response, cart);

            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Item added to cart",
                    cartService.getCartProductsFromCartMeta(cart)
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/increase-quantity")
    public TeknocsResponse<List<CartAction.CartProduct>> increaseQuantity(
            @CookieValue(name = AUTH_COOKIE, required = false) String token,
            @CookieValue(name = CART_COOKIE, required = false, defaultValue = EMPTY_CART) String cartCookie,
            @RequestBody CartAction.Request request,
            HttpServletResponse response)
    {
        try
        {
            if (cartUtils.isAuthenticated(token))
                return new TeknocsResponse<>(
                        HttpStatus.OK,
                        "Quantity increased",
                        cartService.getCartProductsFromCartMeta(cartService.increaseQuantity(token, request.getProductId(), 1))
                );

            Set<User.ShoppingCartData> cart = cartService.increaseGuestQuantity(cartUtils.parseCartCookie(cartCookie), request.getProductId(), 1);
            cartUtils.persistCartCookie(response, cart);

            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Quantity increased",
                    cartService.getCartProductsFromCartMeta(cart)
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/decrease-quantity")
    public TeknocsResponse<List<CartAction.CartProduct>> decreaseQuantity(
            @CookieValue(name = AUTH_COOKIE, required = false) String token,
            @CookieValue(name = CART_COOKIE, required = false, defaultValue = EMPTY_CART) String cartCookie,
            @RequestBody CartAction.Request request,
            HttpServletResponse response)
    {
        try
        {
            if (cartUtils.isAuthenticated(token))
                return new TeknocsResponse<>(
                        HttpStatus.OK,
                        "Quantity decreased",
                        cartService.getCartProductsFromCartMeta(cartService.decreaseQuantity(token, request.getProductId(), 1))
                );

            Set<User.ShoppingCartData> cart = cartService.decreaseGuestQuantity(cartUtils.parseCartCookie(cartCookie), request.getProductId(), 1);
            cartUtils.persistCartCookie(response, cart);

            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Quantity decreased",
                    cartService.getCartProductsFromCartMeta(cart)
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @DeleteMapping("/remove")
    public TeknocsResponse<List<CartAction.CartProduct>> removeFromCart(
            @CookieValue(name = AUTH_COOKIE, required = false) String token,
            @CookieValue(name = CART_COOKIE, required = false, defaultValue = EMPTY_CART) String cartCookie,
            @RequestBody CartAction.Request request,
            HttpServletResponse response)
    {
        try
        {
            if (cartUtils.isAuthenticated(token))
                return new TeknocsResponse<>(
                        HttpStatus.OK,
                        "Item removed from cart",
                        cartService.getCartProductsFromCartMeta(cartService.removeFromCart(token, request.getProductId()))
                );

            Set<User.ShoppingCartData> cart = cartService.removeFromGuestCart(cartUtils.parseCartCookie(cartCookie), request.getProductId());
            cartUtils.persistCartCookie(response, cart);

            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Item removed from cart",
                    cartService.getCartProductsFromCartMeta(cart)
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }
}