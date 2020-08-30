package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDto implements Serializable {
    private Long id;
    private String name;
    private String brand;
    private Double calories;
    private Double whey;
    private Double fat;
    private Double carbohydrates;

}
