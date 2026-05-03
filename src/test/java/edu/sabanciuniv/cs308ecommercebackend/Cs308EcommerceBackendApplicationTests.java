package edu.sabanciuniv.cs308ecommercebackend;

import edu.sabanciuniv.cs308ecommercebackend.controllers.AuthController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.ProductController;
import edu.sabanciuniv.cs308ecommercebackend.controllers.TestController;
import edu.sabanciuniv.cs308ecommercebackend.services.ProductService;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@SpringBootTest
class Cs308EcommerceBackendApplicationTests
{

    // -----------Controllers------------
    @Autowired private AuthController authController;
    @Autowired private ProductController productController;
    @Autowired private TestController testController;
    // ----------------------------------

    // -----------Services------------
    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    // -------------------------------


    @Test
    void contextLoads()
    {
        assert(authController != null);
        assert(productController != null);
        assert(testController != null);

        assert(userService != null);
        assert(productService != null);
    }

    // TODO Test Security
    // TODO Test MongoDB connection sanity (may be dropped)

}
