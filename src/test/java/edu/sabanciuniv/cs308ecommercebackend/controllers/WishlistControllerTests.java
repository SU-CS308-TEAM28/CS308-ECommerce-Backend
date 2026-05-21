package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.wishlist.WishlistAction;
import edu.sabanciuniv.cs308ecommercebackend.services.WishlistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WishlistControllerTests
{

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    @Test
    void doesGetWishlistReturnWishlistProducts() throws Exception
    {
        Set<User.WishlistData> wishlistMeta = Set.of(buildWishlistData("product-1"));
        List<WishlistAction.WishlistProduct> wishlistProducts = List.of(buildWishlistProduct("product-1", "Laptop"));

        when(wishlistService.getWishlist("jwt-token")).thenReturn(wishlistMeta);
        when(wishlistService.getWishlistProductsFromMeta(wishlistMeta)).thenReturn(wishlistProducts);

        var response = wishlistController.getWishlist("jwt-token");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Wishlist retrieved successfully.", body.get("message"));
        assertEquals(wishlistProducts, body.get("data"));

        verify(wishlistService).getWishlist("jwt-token");
        verify(wishlistService).getWishlistProductsFromMeta(wishlistMeta);
    }

    @Test
    void doesGetWishlistReturnInternalServerErrorWhenServiceThrows() throws Exception
    {
        when(wishlistService.getWishlist("jwt-token")).thenThrow(new RuntimeException("Wishlist unavailable"));

        var response = wishlistController.getWishlist("jwt-token");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Wishlist unavailable", body.get("message"));
    }

    @Test
    void doesAddToWishlistReturnUpdatedWishlist() throws Exception
    {
        WishlistAction.Request request = new WishlistAction.Request("product-1");
        Set<User.WishlistData> wishlistMeta = Set.of(buildWishlistData("product-1"));
        List<WishlistAction.WishlistProduct> wishlistProducts = List.of(buildWishlistProduct("product-1", "Laptop"));

        when(wishlistService.addToWishlist("jwt-token", "product-1")).thenReturn(wishlistMeta);
        when(wishlistService.getWishlistProductsFromMeta(wishlistMeta)).thenReturn(wishlistProducts);

        var response = wishlistController.addToWishlist("jwt-token", request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Product with id product-1 added to wishlist.", body.get("message"));
        assertEquals(wishlistProducts, body.get("data"));

        verify(wishlistService).addToWishlist("jwt-token", "product-1");
        verify(wishlistService).getWishlistProductsFromMeta(wishlistMeta);
    }

    @Test
    void doesAddToWishlistReturnBadRequestWhenServiceThrows() throws Exception
    {
        WishlistAction.Request request = new WishlistAction.Request("product-1");

        when(wishlistService.addToWishlist("jwt-token", "product-1"))
                .thenThrow(new Exception("Product is already in wishlist"));

        var response = wishlistController.addToWishlist("jwt-token", request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Product is already in wishlist", body.get("message"));
    }

    @Test
    void doesRemoveFromWishlistReturnUpdatedWishlist() throws Exception
    {
        WishlistAction.Request request = new WishlistAction.Request("product-1");
        Set<User.WishlistData> wishlistMeta = Set.of();
        List<WishlistAction.WishlistProduct> wishlistProducts = List.of();

        when(wishlistService.removeFromWishlist("jwt-token", "product-1")).thenReturn(wishlistMeta);
        when(wishlistService.getWishlistProductsFromMeta(wishlistMeta)).thenReturn(wishlistProducts);

        var response = wishlistController.removeFromWishlist("jwt-token", request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Product with id product-1 removed from wishlist.", body.get("message"));
        assertEquals(wishlistProducts, body.get("data"));

        verify(wishlistService).removeFromWishlist("jwt-token", "product-1");
        verify(wishlistService).getWishlistProductsFromMeta(wishlistMeta);
    }

    @Test
    void doesRemoveFromWishlistReturnBadRequestWhenServiceThrows() throws Exception
    {
        WishlistAction.Request request = new WishlistAction.Request("product-2");

        when(wishlistService.removeFromWishlist("jwt-token", "product-2"))
                .thenThrow(new Exception("Product not found in wishlist"));

        var response = wishlistController.removeFromWishlist("jwt-token", request);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Product not found in wishlist", body.get("message"));
    }

    private User.WishlistData buildWishlistData(String productId)
    {
        return User.WishlistData.builder()
                .productId(productId)
                .dateAdded(new Date())
                .build();
    }

    private WishlistAction.WishlistProduct buildWishlistProduct(String productId, String name)
    {
        return WishlistAction.WishlistProduct.builder()
                .productId(productId)
                .name(name)
                .thumbnailUrl("thumb.png")
                .price(1999.99)
                .activeDiscount(10.0)
                .stock(5)
                .dateAdded(new Date())
                .build();
    }

}
