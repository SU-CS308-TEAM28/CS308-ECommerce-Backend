package edu.sabanciuniv.cs308ecommercebackend;

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

    @Autowired
    public UserService userService;

    @Test
    void contextLoads()
    {
        try {
            userService.createAccount("Test", "User", "test@user.test", "asdasdasd31", Date.from(LocalDate.now().atStartOfDay(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
