package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.wishlist.WishlistAction;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WishlistService
{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private ProductService productService;

    private User getUserFromToken(String token)
    {
        String email = jwtUtils.extractUsername(token);
        return userRepository.findByEmail(email);
    }

    private Set<User.WishlistData> resolveWishlist(User user)
    {
        if (user.getUserData() == null)
            user.setUserData(User.UserData.builder().wishlist(new HashSet<>()).shoppingCart(new HashSet<>()).build());
        if (user.getUserData().getWishlist() == null)
            user.getUserData().setWishlist(new HashSet<>());
        return user.getUserData().getWishlist();
    }

    public Set<User.WishlistData> getWishlist(String token)
    {
        User user = getUserFromToken(token);
        return resolveWishlist(user);
    }

    public Set<User.WishlistData> addToWishlist(String token, String productId) throws Exception
    {
        User user = getUserFromToken(token);
        productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        Set<User.WishlistData> wishlist = resolveWishlist(user);

        boolean alreadyExists = wishlist.stream().anyMatch(item -> item.getProductId().equals(productId));
        if (alreadyExists)
            throw new Exception("Product is already in wishlist");

        wishlist.add(User.WishlistData.builder().productId(productId).dateAdded(new Date()).build());

        user.getUserData().setWishlist(wishlist);
        userRepository.save(user);
        return wishlist;
    }

    public List<WishlistAction.WishlistProduct> getWishlistProductsFromMeta(Set<User.WishlistData> wishlist)
    {
        return wishlist.stream().map(el -> {
            Product product = productService.getProduct(el.getProductId()).orElseThrow();

            return WishlistAction.WishlistProduct.builder()
                    .productId(el.getProductId())
                    .name(product.getName())
                    .thumbnailUrl(product.getThumbnailUrl())
                    .price(product.getPrice())
                    .activeDiscount(product.getActiveDiscount())
                    .stock(product.getStock())
                    .rating(product.getRatings())
                    .dateAdded(el.getDateAdded())
                    .build();
        }).sorted(Comparator.comparing(WishlistAction.WishlistProduct::getName)).toList();
    }

}
