package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserProductsListDto implements Serializable {
    private List<UserProductDto> products = new ArrayList<>();
    private Double totalCalories;
    private Double calorieGoal;
    private Double difference;
}
