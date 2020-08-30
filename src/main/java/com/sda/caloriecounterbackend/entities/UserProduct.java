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


    private LocalDate date;
    private Double weight;



    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class UserProductId implements Serializable {
        private Long userId;
        private Long productId;
    }
}
