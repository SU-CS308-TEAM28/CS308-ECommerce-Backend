package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
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

    public List<Order> getOrdersForUser (User user)
    {
        return orderRepository.findAllByUserId(user.getId());
    }

    public Order placeOrder (User user)
    {
        // TODO Card Check-ups
        Set<User.ShoppingCartData> cartMeta = cartService.getCart(user);

        return orderRepository.save(
                Order.builder()
                        .userId(user.getId())
                        .orderDate(Date.from(Instant.now()))
                        .products(cartMeta)
                        .status("PROCESSING")
                        .deliveryAddress(user.getHomeAddress())
                        .totalPrice(cartService.getCartProductsFromCartMeta(cartMeta).stream().mapToDouble(product -> product.getPrice() * (1.0 - product.getActiveDiscount())).sum())
                        .isCancelled(false)
                        .isCompleted(false)
                        .returnRequest(null)
                        .build()
        );
    }

}
