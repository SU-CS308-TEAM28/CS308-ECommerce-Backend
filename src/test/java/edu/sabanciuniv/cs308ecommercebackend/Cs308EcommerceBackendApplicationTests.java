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
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Cs308EcommerceBackendApplicationTests.TestConfig.class)
class Cs308EcommerceBackendApplicationTests
{
    @Autowired
    private AuthController authController;

    @Autowired
    private ProductController productController;

    @Autowired
    private TestController testController;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Test
    void doControllersAutowired()
    {
        assert authController != null;
        assert productController != null;
        assert testController != null;
    }

    @Test
    void doServicesAutowired()
    {
        assert userService != null;
        assert productService != null;
    }
    
}
