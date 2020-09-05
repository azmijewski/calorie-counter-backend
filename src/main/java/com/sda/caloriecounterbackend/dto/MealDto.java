package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MealDto implements Serializable {
    private Long id;
    private String name;
    private Double calories;
    private Double whey;
    private Double fat;
    private Double carbohydrates;
    private Boolean isDefault;

}
