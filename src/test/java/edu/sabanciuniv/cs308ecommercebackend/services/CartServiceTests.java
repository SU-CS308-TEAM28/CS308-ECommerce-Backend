package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTests
{

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    @Test
    void doesGetCartInitializeMissingUserData()
    {
        User user = User.builder()
                .email("test@unit.run")
                .userData(null)
                .build();

        when(jwtUtils.extractUsername("jwt-token")).thenReturn("test@unit.run");
        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);

        Set<User.ShoppingCartData> result = cartService.getCart("jwt-token");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertNotNull(user.getUserData());
        assertNotNull(user.getUserData().getShoppingCart());
    }

    @Test
    void doesReplaceCartPersistProvidedCart()
    {
        Set<User.ShoppingCartData> newCart = new HashSet<>(Set.of(buildCartData("product-1", 2)));
        User user = buildUser(new HashSet<>());

        when(jwtUtils.extractUsername("jwt-token")).thenReturn("test@unit.run");
        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);

        Set<User.ShoppingCartData> result = cartService.replaceCart("jwt-token", newCart);

        assertSame(newCart, result);
        assertEquals(newCart, user.getUserData().getShoppingCart());
        verify(userRepository).save(user);
    }

    @Test
    void doesAddToCartAddNewItemForAuthenticatedUser() throws Exception
    {
        User user = buildUser(new HashSet<>());
        Product product = buildProduct("product-1", "Laptop", 5);

        when(jwtUtils.extractUsername("jwt-token")).thenReturn("test@unit.run");
        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        Set<User.ShoppingCartData> result = cartService.addToCart("jwt-token", "product-1", 2);

        assertEquals(1, result.size());
        assertTrue(result.contains(buildCartData("product-1", 2)));
        verify(userRepository).save(user);
    }

    @Test
    void doesAddToCartIncreaseExistingQuantity() throws Exception
    {
        User user = buildUser(new HashSet<>(Set.of(buildCartData("product-1", 1))));
        Product product = buildProduct("product-1", "Laptop", 5);

        when(jwtUtils.extractUsername("jwt-token")).thenReturn("test@unit.run");
        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        Set<User.ShoppingCartData> result = cartService.addToCart("jwt-token", "product-1", 2);

        assertEquals(1, result.size());
        assertTrue(result.contains(buildCartData("product-1", 3)));
        verify(userRepository).save(user);
    }

    @Test
    void doesAddToCartThrowWhenStockIsInsufficient() throws Exception
    {
        User user = buildUser(new HashSet<>());
        Product product = buildProduct("product-1", "Laptop", 1);

        when(jwtUtils.extractUsername("jwt-token")).thenReturn("test@unit.run");
        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);
        when(productRepository.findById("product-1")).thenReturn(Optional.of(product));

        Exception exception = assertThrows(Exception.class,
                () -> cartService.addToCart("jwt-token", "product-1", 2));

        assertEquals("Insufficient stock. Available: 1", exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void doesDecreaseQuantityRemoveItemWhenQuantityReachesZero() throws Exception
    {
        User user = buildUser(new HashSet<>(Set.of(buildCartData("product-1", 1))));

        when(jwtUtils.extractUsername("jwt-token")).thenReturn("test@unit.run");
        when(userRepository.findByEmail("test@unit.run")).thenReturn(user);

        Set<User.ShoppingCartData> result = cartService.decreaseQuantity("jwt-token", "product-1", 1);

        assertTrue(result.isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void doesRemoveFromGuestCartThrowWhenCartIsEmpty()
    {
        Exception exception = assertThrows(Exception.class,
                () -> cartService.removeFromGuestCart(new HashSet<>(), "product-1"));

        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    void doesGetCartProductsFromCartMetaReturnSortedProducts()
    {
        Set<User.ShoppingCartData> cart = new HashSet<>(Set.of(
                buildCartData("product-2", 1),
                buildCartData("product-1", 2)
        ));

        when(productService.getProduct("product-1")).thenReturn(Optional.of(buildProduct("product-1", "Laptop", 4)));
        when(productService.getProduct("product-2")).thenReturn(Optional.of(buildProduct("product-2", "Adapter", 8)));

        List<CartAction.CartProduct> result = cartService.getCartProductsFromCartMeta(cart);

        assertEquals(2, result.size());
        assertEquals("Adapter", result.get(0).getName());
        assertEquals("Laptop", result.get(1).getName());
        assertEquals("product-2", result.get(0).getProductId());
        assertEquals(1, result.get(0).getQuantity());
        assertEquals("product-1", result.get(1).getProductId());
        assertEquals(2, result.get(1).getQuantity());
    }

    private User buildUser(Set<User.ShoppingCartData> shoppingCart)
    {
        return User.builder()
                .email("test@unit.run")
                .userData(User.UserData.builder()
                        .wishlist(new HashSet<>())
                        .shoppingCart(shoppingCart)
                        .build())
                .build();
    }

    private User.ShoppingCartData buildCartData(String productId, int quantity)
    {
        return User.ShoppingCartData.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    private Product buildProduct(String id, String name, int stock)
    {
        return Product.builder()
                .id(id)
                .name(name)
                .price(1999.99)
                .activeDiscount(0.0)
                .stock(stock)
                .thumbnailUrl("thumb.png")
                .ratings(Product.Ratings.builder().count(10).value(4.5).build())
                .build();
    }

}
