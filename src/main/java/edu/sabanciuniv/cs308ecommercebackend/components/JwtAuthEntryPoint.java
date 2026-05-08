package edu.sabanciuniv.cs308ecommercebackend.components;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint
{

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException
    {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.println(mapper.writeValueAsString(Map.of(
                "data", authException.getMessage(),
                "message", "You are not authorized to do this action."
        )));
    }

}
