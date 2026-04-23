package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class TeknocsUserDetailsService implements UserDetailsService
{

    //
    // TODO Some level of abstraction should be implemented to resolve confusions about username<->email disparity.
    //

    @Autowired
    private UserRepository userRepository;

    /**
     * Contrary to the function's name, this returns a specified user by its email. The function had to comply with Spring Security UDS interface.
     *
     * @param email Email of the user to load details of.
     * @return Returns the Teknocs User details.
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        User requestedUser = userRepository.findByEmail(email);

        if (requestedUser == null)
        {
            throw new UsernameNotFoundException("Email not found: " + email);
        }

        return new TeknocsUserDetails(requestedUser.getEmail(), requestedUser.getPwdHash());
    }

    @AllArgsConstructor
    private static class TeknocsUserDetails implements UserDetails
    {
        private String email;
        private String password;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities()
        {
            return List.of();
        }

        @Override
        public String getPassword()
        {
            return password;
        }

        @Override
        public String getUsername()
        {
            return email;
        }

        @Override
        public boolean isCredentialsNonExpired()
        {
            return true; // TODO Check if JWT token expired and return false if did. (This isn't exactly required since the filter has a check up for this in some other way but might be good to have?)
        }
    }

}
