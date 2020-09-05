package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ModifyProductMealDto implements Serializable {
    @NotNull
    private Long productId;
    @NotNull
    private Long mealId;
    @Min(0)
    private Double weight;
}
