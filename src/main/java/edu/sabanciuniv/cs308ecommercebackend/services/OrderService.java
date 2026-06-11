package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.repositories.OrderRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class OrderService
{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    public List<Order> getOrdersOfUser(User user)
    {
        if (user.getUserType().equals("user"))
            return orderRepository.findAllByUserId(user.getId());
        else if (user.getUserType().equals("product_manager"))
            return orderRepository.findAllByIsCompletedFalseAndIsCancelledFalse();
        else
            return orderRepository.findAllByIsCancelledFalse();
    }

    public List<Order> getOrdersOfUserInDateRange(User user, Date start, Date end)
    {
        if (user.getUserType().equals("user"))
            return orderRepository.findAllByUserIdAndOrderDateBetween(user.getId(), start, end);
        else if (user.getUserType().equals("product_manager"))
            return orderRepository.findAllByIsCompletedFalseAndIsCancelledFalseAndOrderDateBetween(start, end);
        else
            return orderRepository.findAllByIsCancelledFalseAndOrderDateBetween(start, end);
    }

    public Order getOrder(String orderId)
    {
        return orderRepository.findById(orderId).orElseThrow();
    }

    public Order getOrderOfUser(User user, String orderId)
    {
        return orderRepository.findByIdAndUserId(orderId, user.getId());
    }

    public Order getOrderOfUser(String productId, User user)
    {
        return orderRepository.findByProductsProductIdAndUserId(productId, user.getId());
    }

    public Order updateOrder(String orderId, String newStatus) throws Exception
    {
        Order order = orderRepository.findById(orderId).orElseThrow();

        // TODO Check if we are going back in the loop??
        if (newStatus.equals("PROCESSING") || newStatus.equals("IN_TRANSIT"))
            order.setStatus(newStatus);
        else if (newStatus.equals("DELIVERED"))
        {
            order.setStatus(newStatus);
            order.setCompleted(true);
        }
        else
            throw new Exception("Illegal state of order detected.");

        return orderRepository.save(order);
    }

    public Order cancelOrderOfUser(User user, String orderId)
    {
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId());
        order.setCancelled(true);

        Set<User.ShoppingCartData> cartMeta = cartService.getCart(user);

        List<Product> dirtyProducts = new ArrayList<Product>();
        for (User.ShoppingCartData cartData : cartMeta)
        {
            Product product = productService.getProduct(cartData.getProductId()).orElseThrow();

            product.setStock(product.getStock() + cartData.getQuantity());

            dirtyProducts.add(product);
        }
        productRepository.saveAll(dirtyProducts);

        return orderRepository.save(order);
    }

    public Order placeOrder(User user) throws Exception
    {
        // TODO Card check-ups
        Set<User.ShoppingCartData> cartMeta = cartService.getCart(user);
        List<CartAction.CartProduct> products = cartService.getCartProductsFromCartMeta(cartMeta);

        List<Product> dirtyProducts = new ArrayList<Product>();
        for (User.ShoppingCartData cartData : cartMeta)
        {
            // TODO PROPER CHECK UP
            Product product = productService.getProduct(cartData.getProductId()).orElseThrow();

            if (product.getStock() < cartData.getQuantity())
                throw new Exception("The stock on one or more item has changed and there are items with not enough stock. Try reloading the page.");

            product.setStock(product.getStock() - cartData.getQuantity());

            dirtyProducts.add(product);

            cartData.setPrice(product.getPrice() * (1.0 - product.getActiveDiscount() / 100.0) * cartData.getQuantity());
        }
        productRepository.saveAll(dirtyProducts);

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
                        .build()
        );
    }
}