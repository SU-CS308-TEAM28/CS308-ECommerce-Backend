package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.order.GetOrders;
import edu.sabanciuniv.cs308ecommercebackend.services.CartService;
import edu.sabanciuniv.cs308ecommercebackend.services.OrderService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController
{

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public TeknocsResponse<List<GetOrders.OrderData>> getOrders(@CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token)
    {
        List<GetOrders.OrderData> orders = orderService.getOrdersForUser(userService.getUserByToken(token))
                .stream().map(order -> GetOrders.OrderData.builder()
                        .id(order.getId())
                        .userId(order.getUserId())
                        .orderDate(order.getOrderDate())
                        .products(cartService.getCartProductsFromCartMeta(order.getProducts()))
                        .status(order.getStatus())
                        .totalPrice(order.getTotalPrice())
                        .deliveryAddress(order.getDeliveryAddress())
                        .isCompleted(order.isCompleted())
                        .isCancelled(order.isCancelled())
                        .build()
        ).toList();

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Orders returned successfully.",
                orders
        );
    }

}
