package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@NamedQueries({
        @NamedQuery(name = "userProduct.FindByDateAndUserName", query = "SELECT up FROM UserProduct up " +
                "where up.id.date = :date and up.user.username = :username"),
        @NamedQuery(name = "userProduct.FindByProductAndDateAndUsername", query = "SELECT up from UserProduct up where " +
                "up.product.id = :id and up.user.username = :username and up.id.date = :date")
})

public class UserProduct {

    @EmbeddedId
    private UserProductId id = new UserProductId();

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("productId")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;



    private Double weight;



    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class UserProductId implements Serializable {
        private Long userId;
        private Long productId;
        private LocalDate date;
    }
}
