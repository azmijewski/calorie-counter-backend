package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.MealDto;
import com.sda.caloriecounterbackend.dto.MealWithProductsDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface MealService {
    ResponseEntity<Page<MealDto>> getDefaultAndUserMeals(String username, int page, int size);
    ResponseEntity<MealWithProductsDto> getMealById(Long mealId);
    ResponseEntity<?> saveMeal(String username, MealDto mealDto);
    ResponseEntity<?> editMeal(Long mealId, MealDto mealDto);
    ResponseEntity<?> deleteMeal(Long mealId);
    ResponseEntity<?> addProductToMeal(Long productId, Long mealId, Double weight);
    ResponseEntity<?> deleteProductFromMeal(Long productId, Long mealId);
    ResponseEntity<?> modifyProductWeightInMeal(Long productId, Long mealId, Double newWeight);
}
