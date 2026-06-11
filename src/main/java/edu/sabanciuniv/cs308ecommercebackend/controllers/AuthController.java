package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth.AuthLogin;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.auth.AuthRegister;
import edu.sabanciuniv.cs308ecommercebackend.services.UserService;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtils jwtUtil;

    @PostMapping("/register")
    public TeknocsResponse<AuthRegister.Response> register(@RequestBody AuthRegister.Request request)
    {
        // TODO Email regex rules
        // TODO Password rules

        try
        {
            User newUser = userService.createAccount(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), request.getBirthDate());
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, "Unknown error occurred while creating account.", null);
        }

        return new TeknocsResponse<>(HttpStatus.OK, "User %s registered successfully.".formatted(request.getEmail()), null);
    }

    @PostMapping("/login")
    public TeknocsResponse<AuthLogin.Response> login(@RequestBody AuthLogin.Request request, HttpServletResponse response)
    {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        try
        {
            authenticationManager.authenticate(authentication);
        }
        catch (BadCredentialsException e)
        {
            return new TeknocsResponse<>(HttpStatus.BAD_REQUEST, "Invalid email or password!", null);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        User user = userService.getUserByEmail(userDetails.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername(), user.getUserType());

        ResponseCookie cookie = ResponseCookie.from("_TCS_AUTH", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2100000)
                .sameSite(SameSiteCookies.STRICT.toString())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "User logged in successfully.",
                AuthLogin.Response
                        .builder()
                        .token(token)
                        .user(user)
                        .build()
        );
    }

    @GetMapping("/check-auth")
    public TeknocsResponse<AuthLogin.Response> checkAuthentication(@CookieValue(name = "_TCS_AUTH", defaultValue = "NOT_AUTH") String token)
    {
        if (token.equals("NOT_AUTH"))
            return new TeknocsResponse<>(HttpStatus.UNAUTHORIZED, "User is not authorized.", null);

        AuthLogin.Response response = AuthLogin.Response
                .builder()
                .token(token)
                .user(userService.getUserByToken(token))
                .build();

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Authentication valid, logged in as %s.".formatted(response.getUser().getEmail()),
                response
        );
    }

}
