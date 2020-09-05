package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.MealProduct;

import java.util.Optional;

public interface MealProductDao {
    void save(MealProduct mealProduct);
    void modify(MealProduct mealProduct);
    void delete(MealProduct mealProduct);
    Optional<MealProduct> getByProductIdAndMealId(Long productId, Long mealId);
}
