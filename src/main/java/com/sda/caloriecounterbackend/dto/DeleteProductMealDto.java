package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeleteProductMealDto implements Serializable {
    @NotNull
    private Long productId;
    @NotNull
    private Long mealId;
}
