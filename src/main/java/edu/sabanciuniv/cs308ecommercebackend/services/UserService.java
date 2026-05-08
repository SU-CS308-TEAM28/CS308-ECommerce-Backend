package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import edu.sabanciuniv.cs308ecommercebackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;

@Service
public class UserService
{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    public User createAccount(String firstName, String lastName, String email, String password, Date birthDate) throws Exception
    {
        // TODO Check if user already exists to prevent overwrite!!!

        return userRepository.save(User.builder()
                .name(firstName)
                .surname(lastName)
                .email(email)
                .taxId(null)
                .homeAddress(null)
                .pwdHash(passwordEncoder.encode(password))
                .birthDate(birthDate)
                .userType("user")
                .userData(
                        User.UserData.builder()
                        .wishlist(new HashSet<>())
                        .shoppingCart(new HashSet<>())
                        .build()
                )
                .build()
        );
    }

    public boolean isUserProfileCompleted(User user)
    {
        return user.getHomeAddress() != null && !user.getHomeAddress().isBlank() &&
                user.getTaxId() != null && !user.getTaxId().isBlank();
    }

    public User changeUserHomeAddress(String token, String homeAddress)
    {
        String email = jwtUtils.extractUsername(token);
        User user = userRepository.findByEmail(email);
        user.setHomeAddress(homeAddress);
        user = userRepository.save(user);
        user.setPwdHash("");

        return user;
    }

    public User changeUserTaxId(String token, String taxId)
    {
        // TODO CHECK UP TAX LENGTH TO 11
        String email = jwtUtils.extractUsername(token);
        User user = userRepository.findByEmail(email);
        user.setTaxId(taxId);
        user = userRepository.save(user);
        user.setPwdHash("");

        return user;
    }

    public User getUserByEmail(String email)
    {
        User user = userRepository.findByEmail(email);
        user.setPwdHash("");

        return user;
    }

    public User getUserByToken(String token)
    {
        if (token.startsWith("Bearer"))
            token = token.substring(7);

        return getUserByEmail(jwtUtils.extractUsername(token));
    }

}
