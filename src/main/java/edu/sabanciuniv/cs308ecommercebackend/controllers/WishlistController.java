package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.wishlist.WishlistAction;
import edu.sabanciuniv.cs308ecommercebackend.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.sabanciuniv.cs308ecommercebackend.utils.CartUtils.AUTH_COOKIE;

@RestController
@RequestMapping("/api/user/wishlist")
public class WishlistController
{

    @Autowired
    private WishlistService wishlistService;

}
