package edu.sabanciuniv.cs308ecommercebackend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TestControllerTest {

    private final TestController testController = new TestController();

    @Test
    public void shouldReturnOk() {
        var response = testController.test();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("message")).isEqualTo("Test endpoint request successful.");
        assertThat(((Map<String, Object>) response.getBody().get("data"))).containsKey("timestamp");
    }
}
