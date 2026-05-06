package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JWTUtils jwtUtils;

    private User getUserFromToken(String token) {
        String email = jwtUtils.extractUsername(token);
        return userRepository.findByEmail(email);
    }

    private Set<User.ShoppingCartData> resolveCart(User user) {
        if (user.getUserData() == null)
            user.setUserData(User.UserData.builder().wishlist(new HashSet<>()).shoppingCart(new HashSet<>()).build());
        if (user.getUserData().getShoppingCart() == null)
            user.getUserData().setShoppingCart(new HashSet<>());
        return user.getUserData().getShoppingCart();
    }

    public Set<User.ShoppingCartData> getCart(String token) {
        User user = getUserFromToken(token);
        return resolveCart(user);
    }

    public Set<User.ShoppingCartData> addToCart(String token, String productId, Integer quantity) throws Exception {
        User user = getUserFromToken(token);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        if (product.getStock() == 0)
            throw new Exception("Product is out of stock");

        if (quantity == null || quantity <= 0)
            throw new Exception("Quantity must be greater than zero");

        Set<User.ShoppingCartData> cart = resolveCart(user);

        Optional<User.ShoppingCartData> existing = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + quantity;
            if (product.getStock() < newQty)
                throw new Exception("Insufficient stock. Available: " + product.getStock());
            cart.remove(existing.get());
            cart.add(User.ShoppingCartData.builder().productId(productId).quantity(newQty).build());
        } else {
            if (product.getStock() < quantity)
                throw new Exception("Insufficient stock. Available: " + product.getStock());
            cart.add(User.ShoppingCartData.builder().productId(productId).quantity(quantity).build());
        }

        user.getUserData().setShoppingCart(cart);
        userRepository.save(user);
        return cart;
    }

    public Set<User.ShoppingCartData> increaseQuantity(String token, String productId, Integer amount) throws Exception {
        User user = getUserFromToken(token);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        Set<User.ShoppingCartData> cart = resolveCart(user);

        User.ShoppingCartData item = cart.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new Exception("Item not found in cart"));

        int newQty = item.getQuantity() + amount;
        if (product.getStock() < newQty)
            throw new Exception("Insufficient stock. Available: " + product.getStock());

        cart.remove(item);
        cart.add(User.ShoppingCartData.builder().productId(productId).quantity(newQty).build());

        user.getUserData().setShoppingCart(cart);
        userRepository.save(user);
        return cart;
    }

    public Set<User.ShoppingCartData> decreaseQuantity(String token, String productId, Integer amount) throws Exception {
        User user = getUserFromToken(token);

        Set<User.ShoppingCartData> cart = resolveCart(user);

        User.ShoppingCartData item = cart.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new Exception("Item not found in cart"));

        int newQty = item.getQuantity() - amount;
        if (newQty <= 0) {
            cart.remove(item);
        } else {
            cart.remove(item);
            cart.add(User.ShoppingCartData.builder().productId(productId).quantity(newQty).build());
        }

        user.getUserData().setShoppingCart(cart);
        userRepository.save(user);
        return cart;
    }

    public Set<User.ShoppingCartData> removeFromCart(String token, String productId) throws Exception {
        User user = getUserFromToken(token);

        Set<User.ShoppingCartData> cart = resolveCart(user);
        if (cart.isEmpty()) throw new Exception("Cart is empty");

        boolean removed = cart.removeIf(item -> item.getProductId().equals(productId));
        if (!removed) throw new Exception("Item not found in cart");

        user.getUserData().setShoppingCart(cart);
        userRepository.save(user);
        return cart;
    }
}
