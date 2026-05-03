package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class TestControllerTests
{

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void doesTestReturnValidTimestampInTime ()
    {
        Map<?, ?> responseBody = restTestClient.get()
                .uri("http://localhost:%d/api/test".formatted(port))
                .exchange()
                .expectBody(Map.class)
                .returnResult().getResponseBody();

        assert responseBody != null;
        assert responseBody.get("data") instanceof Map;
        assert ((Map<?, ?>)responseBody.get("data")).get("timestamp") instanceof String;
        assert Math.abs(Long.parseUnsignedLong(((String) ((Map<?, ?>)responseBody.get("data")).get("timestamp"))) - System.currentTimeMillis()) < 5000;
    }

}
