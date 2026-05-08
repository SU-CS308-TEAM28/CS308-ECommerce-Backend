package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.order.GetOrders;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.order.PlaceOrder;
import edu.sabanciuniv.cs308ecommercebackend.services.CartService;
import edu.sabanciuniv.cs308ecommercebackend.services.InvoiceService;
import edu.sabanciuniv.cs308ecommercebackend.services.MailService;
import edu.sabanciuniv.cs308ecommercebackend.services.OrderService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController
{

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private MailService mailService;

    @GetMapping("/orders")
    public TeknocsResponse<List<GetOrders.OrderData>> getOrders(@CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token)
    {
        List<GetOrders.OrderData> orders = orderService.getOrdersOfUser(userService.getUserByToken(token))
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

    @GetMapping("/{id}")
    public TeknocsResponse<GetOrders.OrderData> getOrder(@CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token, @PathVariable String id)
    {
        Order order = orderService.getOrderOfUser(userService.getUserByToken(token), id);
        GetOrders.OrderData orderData = GetOrders.OrderData.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .products(cartService.getCartProductsFromCartMeta(order.getProducts()))
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .isCompleted(order.isCompleted())
                .isCancelled(order.isCancelled())
                .build();

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Order returned successfully.",
                orderData
        );
    }

    @PostMapping("/place")
    public TeknocsResponse<String> placeOrder(
            @CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token,
            @RequestBody PlaceOrder.Request request)
    {
        try
        {
            User user = userService.getUserByToken(token);
            if (user == null)
            {
                return new TeknocsResponse<>(HttpStatus.UNAUTHORIZED, "Authentication required", null);
            }

            Order order = null;
            try
            {
                order = orderService.placeOrder(user);
                cartService.replaceCart(token, null);
            }
            catch (Exception e)
            {
                return new TeknocsResponse<>(
                        HttpStatus.BAD_REQUEST,
                        e.getMessage(),
                        null
                );
            }

            try
            {
                List<CartAction.CartProduct> products =
                        cartService.getCartProductsFromCartMeta(order.getProducts());

                byte[] pdf = invoiceService.generateInvoicePdf(order, user, products);
                String filename = "invoice-" + order.getId() + ".pdf";
                String html = buildEmailBody(user);

                mailService.sendInvoiceEmail(
                        user.getEmail(),
                        "Your Teknocs order",
                        html,
                        pdf,
                        filename
                );
            }
            catch (Exception e)
            {
                log.warn("Failed to send invoice email for order {}: {}",
                        order.getId(), e.getMessage(), e);
            }

            return new TeknocsResponse<>(
                    HttpStatus.OK,
                    "Order placed",
                    order.getId()
            );
        }
        catch (Exception e)
        {
            log.error("Failed to place order", e);
            return new TeknocsResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

    private String buildEmailBody(User user)
    {
        String name = user.getName() != null ? user.getName() : "there";
        return """
            <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif; max-width: 560px; margin: 0 auto; padding: 24px; color: #111827;">
              <h2 style="margin: 0 0 16px 0; font-size: 22px;">Order confirmed!</h2>
              <p style="margin: 0 0 12px 0; line-height: 1.6;">Hi %s,</p>
              <p style="margin: 0 0 12px 0; line-height: 1.6;">Thanks for shopping with Teknocs. Your order has been placed and is now being processed.</p>
              <p style="margin: 0 0 12px 0; line-height: 1.6;">A copy of your invoice is attached to this email as a PDF.</p>
              <p style="margin: 24px 0 0 0; color: #6b7280; font-size: 13px;">— The Teknocs team</p>
            </div>
            """.formatted(name);
    }
}