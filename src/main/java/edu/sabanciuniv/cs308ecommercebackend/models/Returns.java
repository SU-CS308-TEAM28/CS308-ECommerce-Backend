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
@Document(collection = "Returns")
public class Returns
{
    @Id
    String id;
    String orderId;
    Set<User.ShoppingCartData> returningProducts;
    String reason;
    Date requestDate;
    boolean isApproved;
    boolean isCompleted;
}
