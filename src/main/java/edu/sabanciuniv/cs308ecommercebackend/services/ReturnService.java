package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Order;
import edu.sabanciuniv.cs308ecommercebackend.models.Returns;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.returns.ReturnRequest;
import edu.sabanciuniv.cs308ecommercebackend.repositories.OrderRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ReturnRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReturnService
{
    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private MailService mailService;

    public Returns requestReturn(User user, String orderId, List<ReturnRequest.Request.ProductReturn> returningProducts, String reason) throws Exception
    {
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (order == null)
            throw new Exception("Order not found.");
        if (!order.isCompleted())
            throw new Exception("Only completed orders can be returned.");

        Set<User.ShoppingCartData> returnSet = new HashSet<>();
        for (ReturnRequest.Request.ProductReturn item : returningProducts)
        {
            User.ShoppingCartData orderProduct = order.getProducts().stream()
                    .filter(p -> p.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Product " + item.getProductId() + " was not part of this order."));

            if (item.getQuantity() > orderProduct.getQuantity())
                throw new Exception("Return quantity exceeds purchased quantity for product " + item.getProductId() + ".");

            double refundAmount = (orderProduct.getPrice() / orderProduct.getQuantity()) * item.getQuantity();
            returnSet.add(User.ShoppingCartData.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(refundAmount)
                    .build());
        }

        return returnRepository.save(Returns.builder()
                .orderId(orderId)
                .returningProducts(returnSet)
                .reason(reason)
                .requestDate(Date.from(Instant.now()))
                .isApproved(false)
                .isCompleted(false)
                .build());
    }

    public Returns approveReturn(String returnId)
    {
        Returns returns = returnRepository.findById(returnId).orElseThrow();
        returns.setApproved(true);
        return returnRepository.save(returns);
    }

    public Returns completeReturn(String returnId)
    {
        Returns returns = returnRepository.findById(returnId).orElseThrow();
        returns.setCompleted(true);
        Returns saved = returnRepository.save(returns);

        try
        {
            Order order = orderRepository.findById(saved.getOrderId()).orElseThrow();
            User user = userRepository.findById(order.getUserId()).orElseThrow();

            List<CartAction.CartProduct> items = saved.getReturningProducts().stream()
                    .map(p -> CartAction.CartProduct.builder()
                            .productId(p.getProductId())
                            .name(productService.getProduct(p.getProductId())
                                    .map(prod -> prod.getName())
                                    .orElse(p.getProductId()))
                            .quantity(p.getQuantity())
                            .price(p.getPrice())
                            .build())
                    .toList();

            double totalRefund = items.stream().mapToDouble(CartAction.CartProduct::getPrice).sum();

            mailService.sendRefundNotificationEmail(user.getEmail(), user.getName(), items, totalRefund);
        }
        catch (Exception ignored) {}

        return saved;
    }

    public List<Returns> getReturns(User user)
    {
        if (user.getUserType().equals("user"))
        {
            List<String> orderIds = orderRepository.findAllByUserId(user.getId())
                    .stream().map(Order::getId).toList();
            return returnRepository.findAllByOrderIdIn(orderIds);
        }
        return returnRepository.findAll();
    }

    public boolean isReturnRequestedForOrder(String orderId)
    {
        return returnRepository.findByOrderId(orderId).isPresent();
    }
}
