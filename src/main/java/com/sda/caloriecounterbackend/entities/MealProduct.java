package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@NamedQueries({
        @NamedQuery(name = "mealProduct.FindByProductIdAndMealId", query = "SELECT mp FROM MealProduct mp " +
                "where mp.product.id = :productId and mp.meal.id = :mealId")
})
public class MealProduct {

    @EmbeddedId
    private MealProductId id = new MealProductId();

    @MapsId("mealId")
    @ManyToOne
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @MapsId("productId")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Double weight;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class MealProductId implements Serializable {
        private Long mealId;
        private Long productId;
    }
}
