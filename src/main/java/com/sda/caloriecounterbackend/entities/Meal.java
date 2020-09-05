package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@NamedQueries({
        @NamedQuery(name = "meal.FindByUsernameAndDefaults", query = "SELECT m FROM Meal m" +
                " where m.user.username = :username or m.isDefault = true"),
        @NamedQuery(name = "meal.FindByIdJoinProducts", query = "SELECT m FROM Meal m left join fetch m.mealProducts mp " +
                "left join fetch mp.product where m.id = :id")
})
@Indexed
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Field
    private String name;
    private Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @IndexedEmbedded
    private User user;

    @OneToMany(mappedBy = "meal", orphanRemoval = true)
    private List<MealProduct> mealProducts = new ArrayList<>();
}
