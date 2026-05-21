package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.services.CartService;
import edu.sabanciuniv.cs308ecommercebackend.utils.CartUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartControllerTests
{

    @Mock
    private CartUtils cartUtils;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletResponse servletResponse;

    @InjectMocks
    private CartController cartController;

    @Test
    void doesGetCartMergeGuestCartForAuthenticatedUser() throws Exception
    {
        Set<User.ShoppingCartData> guestCart = Set.of(buildCartData("product-1", 2));
        Set<User.ShoppingCartData> mergedCart = Set.of(buildCartData("product-1", 3));
        List<CartAction.CartProduct> cartProducts = List.of(buildCartProduct("product-1", "Laptop", 3));

        when(cartUtils.isAuthenticated("jwt-token")).thenReturn(true);
        when(cartUtils.parseCartCookie("guest-cookie")).thenReturn(guestCart);
        when(cartService.replaceCart("jwt-token", guestCart)).thenReturn(mergedCart);
        when(cartService.getCartProductsFromCartMeta(mergedCart)).thenReturn(cartProducts);

        var response = cartController.getCart("jwt-token", "guest-cookie", servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Cart retrieved", body.get("message"));
        assertEquals(cartProducts, body.get("data"));

        verify(cartService).replaceCart("jwt-token", guestCart);
        verify(cartUtils).clearCartCookie(servletResponse);
    }

    @Test
    void doesGetCartReturnStoredCartForAuthenticatedUserWithoutGuestItems() throws Exception
    {
        Set<User.ShoppingCartData> emptyGuestCart = Set.of();
        Set<User.ShoppingCartData> storedCart = Set.of(buildCartData("product-2", 1));
        List<CartAction.CartProduct> cartProducts = List.of(buildCartProduct("product-2", "Tablet", 1));

        when(cartUtils.isAuthenticated("jwt-token")).thenReturn(true);
        when(cartUtils.parseCartCookie("[]")).thenReturn(emptyGuestCart);
        when(cartService.getCart("jwt-token")).thenReturn(storedCart);
        when(cartService.getCartProductsFromCartMeta(storedCart)).thenReturn(cartProducts);

        var response = cartController.getCart("jwt-token", "[]", servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Cart retrieved", body.get("message"));
        assertEquals(cartProducts, body.get("data"));

        verify(cartService).getCart("jwt-token");
        verify(cartUtils, never()).clearCartCookie(servletResponse);
    }

    @Test
    void doesGetCartReturnGuestCartWhenUserIsNotAuthenticated() throws Exception
    {
        Set<User.ShoppingCartData> guestCart = Set.of(buildCartData("product-3", 2));
        List<CartAction.CartProduct> cartProducts = List.of(buildCartProduct("product-3", "Phone", 2));

        when(cartUtils.isAuthenticated(null)).thenReturn(false);
        when(cartUtils.parseCartCookie("guest-cookie")).thenReturn(guestCart);
        when(cartService.getGuestCart(guestCart)).thenReturn(guestCart);
        when(cartService.getCartProductsFromCartMeta(guestCart)).thenReturn(cartProducts);

        var response = cartController.getCart(null, "guest-cookie", servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Cart retrieved", body.get("message"));
        assertEquals(cartProducts, body.get("data"));
    }

    @Test
    void doesAddToCartUseAuthenticatedFlow() throws Exception
    {
        CartAction.Request request = new CartAction.Request("product-1");
        Set<User.ShoppingCartData> updatedCart = Set.of(buildCartData("product-1", 1));
        List<CartAction.CartProduct> cartProducts = List.of(buildCartProduct("product-1", "Laptop", 1));

        when(cartUtils.isAuthenticated("jwt-token")).thenReturn(true);
        when(cartService.addToCart("jwt-token", "product-1", 1)).thenReturn(updatedCart);
        when(cartService.getCartProductsFromCartMeta(updatedCart)).thenReturn(cartProducts);

        var response = cartController.addToCart("jwt-token", "[]", request, servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Item added to cart", body.get("message"));
        assertEquals(cartProducts, body.get("data"));

        verify(cartService).addToCart("jwt-token", "product-1", 1);
        verify(cartUtils, never()).persistCartCookie(servletResponse, updatedCart);
    }

    @Test
    void doesAddToCartPersistCookieForGuestUser() throws Exception
    {
        CartAction.Request request = new CartAction.Request("product-1");
        Set<User.ShoppingCartData> guestCart = Set.of(buildCartData("product-1", 1));
        List<CartAction.CartProduct> cartProducts = List.of(buildCartProduct("product-1", "Laptop", 1));

        when(cartUtils.isAuthenticated(null)).thenReturn(false);
        when(cartUtils.parseCartCookie("[]")).thenReturn(Set.of());
        when(cartService.addToGuestCart(Set.of(), "product-1", 1)).thenReturn(guestCart);
        when(cartService.getCartProductsFromCartMeta(guestCart)).thenReturn(cartProducts);

        var response = cartController.addToCart(null, "[]", request, servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Item added to cart", body.get("message"));
        assertEquals(cartProducts, body.get("data"));

        verify(cartUtils).persistCartCookie(servletResponse, guestCart);
    }

    @Test
    void doesIncreaseQuantityUseAuthenticatedFlow() throws Exception
    {
        CartAction.Request request = new CartAction.Request("product-1");
        Set<User.ShoppingCartData> updatedCart = Set.of(buildCartData("product-1", 2));
        List<CartAction.CartProduct> cartProducts = List.of(buildCartProduct("product-1", "Laptop", 2));

        when(cartUtils.isAuthenticated("jwt-token")).thenReturn(true);
        when(cartService.increaseQuantity("jwt-token", "product-1", 1)).thenReturn(updatedCart);
        when(cartService.getCartProductsFromCartMeta(updatedCart)).thenReturn(cartProducts);

        var response = cartController.increaseQuantity("jwt-token", "[]", request, servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Quantity increased", body.get("message"));
        assertEquals(cartProducts, body.get("data"));
    }

    @Test
    void doesDecreaseQuantityPersistCookieForGuestUser() throws Exception
    {
        CartAction.Request request = new CartAction.Request("product-1");
        Set<User.ShoppingCartData> guestCart = Set.of(buildCartData("product-1", 1));
        List<CartAction.CartProduct> cartProducts = List.of(buildCartProduct("product-1", "Laptop", 1));

        when(cartUtils.isAuthenticated(null)).thenReturn(false);
        when(cartUtils.parseCartCookie("guest-cookie")).thenReturn(Set.of(buildCartData("product-1", 2)));
        when(cartService.decreaseGuestQuantity(Set.of(buildCartData("product-1", 2)), "product-1", 1)).thenReturn(guestCart);
        when(cartService.getCartProductsFromCartMeta(guestCart)).thenReturn(cartProducts);

        var response = cartController.decreaseQuantity(null, "guest-cookie", request, servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Quantity decreased", body.get("message"));
        assertEquals(cartProducts, body.get("data"));

        verify(cartUtils).persistCartCookie(servletResponse, guestCart);
    }

    @Test
    void doesRemoveFromCartReturnBadRequestWhenServiceThrows() throws Exception
    {
        CartAction.Request request = new CartAction.Request("missing-product");

        when(cartUtils.isAuthenticated("jwt-token")).thenReturn(true);
        when(cartService.removeFromCart("jwt-token", "missing-product")).thenThrow(new RuntimeException("Item not found in cart"));

        var response = cartController.removeFromCart("jwt-token", "[]", request, servletResponse);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Item not found in cart", body.get("message"));
        assertInstanceOf(Object.class, body.get("data"));
    }

    private User.ShoppingCartData buildCartData(String productId, int quantity)
    {
        return User.ShoppingCartData.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    private CartAction.CartProduct buildCartProduct(String productId, String name, int quantity)
    {
        return CartAction.CartProduct.builder()
                .productId(productId)
                .name(name)
                .quantity(quantity)
                .price(1999.99)
                .activeDiscount(0.0)
                .stock(10)
                .thumbnailUrl("thumb.png")
                .build();
    }

}
