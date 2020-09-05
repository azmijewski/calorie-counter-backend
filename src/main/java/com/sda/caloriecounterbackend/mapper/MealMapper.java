package com.sda.caloriecounterbackend.mapper;

import com.sda.caloriecounterbackend.dto.MealDto;
import com.sda.caloriecounterbackend.entities.Meal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MealMapper {
    MealDto mapToDto(Meal meal);
    Meal mapToDb(MealDto mealDto);
}
