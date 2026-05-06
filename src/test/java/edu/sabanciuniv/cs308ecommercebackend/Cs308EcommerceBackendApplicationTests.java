package edu.sabanciuniv.cs308ecommercebackend;

import edu.sabanciuniv.cs308ecommercebackend.controllers.AuthController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.ProductController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.TestController;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
