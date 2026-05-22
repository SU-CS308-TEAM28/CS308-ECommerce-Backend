package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Returns;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.cart.CartAction;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.returns.GetReturns;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.returns.ReturnAction;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.returns.ReturnRequest;
import edu.sabanciuniv.cs308ecommercebackend.services.CartService;
import edu.sabanciuniv.cs308ecommercebackend.services.ReturnService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return")
public class ReturnController
{
    @Autowired
    private ReturnService returnService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @PostMapping("/request")
    public TeknocsResponse<Returns> requestReturn(
            @RequestBody ReturnRequest.Request request,
            @CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token)
    {
        try
        {
            User user = userService.getUserByToken(token);
            Returns returns = returnService.requestReturn(user, request.getOrderId(), request.getReturningProducts(), request.getReason());
            return new TeknocsResponse<>(HttpStatus.CREATED, "Return request submitted successfully.", returns);
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/approve")
    public TeknocsResponse<Returns> approveReturn(@RequestBody ReturnAction.Request request)
    {
        try
        {
            return new TeknocsResponse<>(HttpStatus.OK, "Return approved.", returnService.approveReturn(request.getReturnId()));
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.NOT_FOUND, "Return not found.", null);
        }
    }

    @PutMapping("/complete")
    public TeknocsResponse<Returns> completeReturn(@RequestBody ReturnAction.Request request)
    {
        try
        {
            return new TeknocsResponse<>(HttpStatus.OK, "Return completed.", returnService.completeReturn(request.getReturnId()));
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.NOT_FOUND, "Return not found.", null);
        }
    }

    @GetMapping("/returns")
    public TeknocsResponse<List<GetReturns.ReturnData>> getReturns(
            @CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token)
    {
        User user = userService.getUserByToken(token);

        List<GetReturns.ReturnData> returns = returnService.getReturns(user).stream()
                .map(ret -> {
                    List<CartAction.CartProduct> products = cartService.getCartProductsFromCartMeta(ret.getReturningProducts());
                    return GetReturns.ReturnData.builder()
                            .id(ret.getId())
                            .orderId(ret.getOrderId())
                            .products(products)
                            .reason(ret.getReason())
                            .requestDate(ret.getRequestDate())
                            .isApproved(ret.isApproved())
                            .isCompleted(ret.isCompleted())
                            .build();
                })
                .toList();

        return new TeknocsResponse<>(HttpStatus.OK, "Returns retrieved successfully.", returns);
    }
}
