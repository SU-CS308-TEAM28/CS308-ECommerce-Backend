package edu.sabanciuniv.cs308ecommercebackend.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Comments")
public class Comment
{
    @Id
    String id;
    String productId;
    Commenter commenter;
    Date creationDate;
    int rate;
    String comment;
    boolean isApproved;
    boolean isChecked;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Commenter
    {
        String id;
        String publicName;
    }
}
