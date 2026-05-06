package edu.sabanciuniv.cs308ecommercebackend;

import edu.sabanciuniv.cs308ecommercebackend.controllers.AuthController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.ProductController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.TestController;
import edu.sabanciuniv.cs308ecommercebackend.repositories.PagedProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import edu.sabanciuniv.cs308ecommercebackend.services.CommentService;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;

@SpringJUnitConfig(classes = {
        AuthController.class,
        ProductController.class,
        TestController.class,
        UserService.class,
        ProductService.class
})
class Cs308EcommerceBackendApplicationTests
{
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JWTUtils jwtUtils;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private PagedProductRepository pagedProductRepository;

    @MockitoBean
    private CommentService commentService;

    @Test
    void doControllersInstantiate(@Autowired AuthController authController,
                                  @Autowired ProductController productController,
                                  @Autowired TestController testController)
    {
        assert authController != null;
        assert productController != null;
        assert testController != null;
    }

    @Test
    void doServicesInstantiate()
    {
        assert userService != null;
        assert productService != null;
    }

}
