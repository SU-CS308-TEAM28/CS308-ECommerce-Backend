package edu.sabanciuniv.cs308ecommercebackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Orders")
public class Order
{
    @Id
    String id;
    String userId;
    Date orderDate;
    Set<User.ShoppingCartData> products;
    String status;
    double totalPrice;
    String deliveryAddress;
    boolean isCompleted;
    boolean isCancelled;
}
