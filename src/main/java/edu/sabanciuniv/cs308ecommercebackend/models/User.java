package edu.sabanciuniv.cs308ecommercebackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Users")
public class User
{
    @Id
    String id;
    String name;
    String surname;
    String email;
    String homeAddress;
    String pwdHash;
    Date birthDate;
    String userType;
    UserData userData;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserData {
        Set<WishlistData> wishlist;
        Set<ShoppingCartData> shoppingCart;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WishlistData {
        String productId;
        Date dateAdded;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShoppingCartData {
        String productId;
        Integer quantity;
    }
}


