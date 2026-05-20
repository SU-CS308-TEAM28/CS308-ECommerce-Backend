package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.wishlist.WishlistAction;
import edu.sabanciuniv.cs308ecommercebackend.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.sabanciuniv.cs308ecommercebackend.utils.CartUtils.AUTH_COOKIE;

@RestController
@RequestMapping("/api/user/wishlist")
public class WishlistController
{

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public TeknocsResponse<List<WishlistAction.WishlistProduct>> getWishlist(
            @CookieValue(name = AUTH_COOKIE) String token)
    {
        try
        {
            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Wishlist retrieved",
                    wishlistService.getWishlistProductsFromMeta(wishlistService.getWishlist(token))
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/add")
    public TeknocsResponse<List<WishlistAction.WishlistProduct>> addToWishlist(
            @CookieValue(name = AUTH_COOKIE) String token,
            @RequestBody WishlistAction.Request request)
    {
        try
        {
            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Item added to wishlist",
                    wishlistService.getWishlistProductsFromMeta(wishlistService.addToWishlist(token, request.getProductId()))
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @DeleteMapping("/remove")
    public TeknocsResponse<List<WishlistAction.WishlistProduct>> removeFromWishlist(
            @CookieValue(name = AUTH_COOKIE) String token,
            @RequestBody WishlistAction.Request request)
    {
        try
        {
            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Item removed from wishlist",
                    wishlistService.getWishlistProductsFromMeta(wishlistService.removeFromWishlist(token, request.getProductId()))
            );
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

}
