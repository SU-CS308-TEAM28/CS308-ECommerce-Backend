package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService
{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    public List<Order> getOrdersOfUser(User user)
    {
        return orderRepository.findAllByUserId(user.getId());
    }

    public Order getOrderOfUser(User user, String orderId)
    {
        return orderRepository.findByIdAndUserId(orderId, user.getId());
    }

    public Order placeOrder(User user)
    {
        // TODO Card check-ups
        Set<User.ShoppingCartData> cartMeta = cartService.getCart(user);
        List<CartAction.CartProduct> products = cartService.getCartProductsFromCartMeta(cartMeta);

        double totalPrice = products.stream()
                .mapToDouble(p -> p.getPrice()
                        * (1.0 - p.getActiveDiscount() / 100.0)
                        * p.getQuantity())
                .sum();

        return orderRepository.save(
                Order.builder()
                        .userId(user.getId())
                        .orderDate(Date.from(Instant.now()))
                        .products(cartMeta)
                        .status("PROCESSING")
                        .deliveryAddress(user.getHomeAddress())
                        .totalPrice(totalPrice)
                        .isCancelled(false)
                        .isCompleted(false)
                        .returnRequest(null)
                        .build()
        );
    }
}