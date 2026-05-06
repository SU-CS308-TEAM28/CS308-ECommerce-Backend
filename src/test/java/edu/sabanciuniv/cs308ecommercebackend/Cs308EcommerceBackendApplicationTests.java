package edu.sabanciuniv.cs308ecommercebackend;

import edu.sabanciuniv.cs308ecommercebackend.controllers.AuthController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.ProductController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.TestController;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.junit.jupiter.api.Test;

class Cs308EcommerceBackendApplicationTests
{

    @Test
    void doControllersInstantiate()
    {
        AuthController authController = new AuthController();
        ProductController productController = new ProductController();
        TestController testController = new TestController();

        assert authController != null;
        assert productController != null;
        assert testController != null;
    }

    @Test
    void doServicesInstantiate()
    {
        UserService userService = new UserService();
        ProductService productService = new ProductService();

        assert userService != null;
        assert productService != null;
    }

}
