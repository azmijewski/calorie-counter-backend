package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Indexed
@Entity
@NamedQueries({
        @NamedQuery(name = "product.FindAll", query = "select p from Product p")
})


public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Field
    private String name;
    @Field
    private String brand;
    private Double calories;
    private Double whey;
    private Double fat;
    private Double carbohydrates;


    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<UserProduct> userProducts = new ArrayList<>();

}
