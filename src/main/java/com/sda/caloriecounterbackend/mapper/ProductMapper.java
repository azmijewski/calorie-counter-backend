package com.sda.caloriecounterbackend.mapper;

import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.dto.UserProductDto;
import com.sda.caloriecounterbackend.entities.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto mapToDto(Product product);
    Product mapToDb(ProductDto productDto);

    UserProductDto mapToUserProductDto(Product product, Double weight);

    @AfterMapping
    default void addWeightValue(@MappingTarget UserProductDto userProductDto, Double weight) {
        userProductDto.setWeight(weight);
    }
    default void mapDataToUpdate(Product productToModify, ProductDto productDto) {
        productToModify.setName(productDto.getName());
        productToModify.setBrand(productDto.getBrand());
        productToModify.setCalories(productDto.getCalories());
        productToModify.setCarbohydrates(productDto.getCarbohydrates());
        productToModify.setFat(productDto.getFat());
        productToModify.setWhey(productDto.getWhey());
    }
}
