package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserProductDto implements Serializable {
    private String name;
    private String brand;
    private Double weight;
    private Double calories;
    private Double whey;
    private Double fat;
    private Double carbohydrates;
}
