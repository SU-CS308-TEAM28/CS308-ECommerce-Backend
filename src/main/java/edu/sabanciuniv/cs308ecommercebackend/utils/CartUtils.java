package edu.sabanciuniv.cs308ecommercebackend.utils;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Component
public class CartUtils
{
    public static final String AUTH_COOKIE    = "_TCS_AUTH";
    public static final String CART_COOKIE    = "_TCS_CART";
    public static final String EMPTY_CART     = "[]";
    public static final int    COOKIE_MAX_AGE = 60 * 60 * 24 * 30;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isAuthenticated(String token)
    {
        return token != null && !token.isBlank();
    }

    public Set<User.ShoppingCartData> parseCartCookie(String cartCookie) throws Exception
    {
        if (cartCookie == null || cartCookie.isBlank())
            return new HashSet<>();

        String decoded = URLDecoder.decode(cartCookie, StandardCharsets.UTF_8);
        if (decoded.isBlank() || decoded.equals(EMPTY_CART))
            return new HashSet<>();

        return objectMapper.readValue(decoded, new TypeReference<Set<User.ShoppingCartData>>() {});
    }

    public void persistCartCookie(HttpServletResponse response, Set<User.ShoppingCartData> cart) throws Exception
    {
        String encoded = URLEncoder.encode(objectMapper.writeValueAsString(cart), StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(CART_COOKIE, encoded);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public void clearCartCookie(HttpServletResponse response)
    {
        Cookie cookie = new Cookie(CART_COOKIE, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

}
