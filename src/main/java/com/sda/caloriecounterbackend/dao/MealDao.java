package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.Meal;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MealDao {
    Page<Meal> getUserAndDefault(String username, int page, int size);
    Optional<Meal> getById(Long id);
    Meal save(Meal meal);
    void modify(Meal meal);
    void delete(Meal meal);
}
