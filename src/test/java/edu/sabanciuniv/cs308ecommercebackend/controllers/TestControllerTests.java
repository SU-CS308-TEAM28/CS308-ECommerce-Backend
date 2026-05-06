package edu.sabanciuniv.cs308ecommercebackend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
@SpringBootTest
public class TestControllerTests
{

    @Autowired
    private TestController testController;

    @Test
    void doesTestReturnSuccessfulStatusAndMessage ()
    {
        Map<?, ?> responseBody = testController.test().getBody();

        assert testController.test().getStatusCode().is2xxSuccessful();
        assert responseBody != null;
        assert responseBody.get("message").equals("Test endpoint request successful.");
    }

    @Test
    void doesTestReturnValidTimestampInTime ()
    {
        Map<?, ?> responseBody = testController.test().getBody();

        assert responseBody != null;
        assert responseBody.get("data") instanceof Map;
        assert ((Map<?, ?>)responseBody.get("data")).get("timestamp") instanceof String;
        assert Math.abs(Long.parseUnsignedLong(((String) ((Map<?, ?>)responseBody.get("data")).get("timestamp"))) - System.currentTimeMillis()) < 5000;
    }

}
