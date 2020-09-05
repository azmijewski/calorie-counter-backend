package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Date;

public interface UserProductService {
    ResponseEntity<?> addProduct(NewUserProductDto userProductDto, String username);
    ResponseEntity<UserProductsListDto> getAllByDate(LocalDate date, String username);
    ResponseEntity<?> removeProduct(DeleteUserProductDto deleteUserProductDto, String username);
    ResponseEntity<?> modifyUserProduct(ModifyUserProductDto modifyUserProductDto, String username);
    ResponseEntity<?> addMeal(NewUserMealDto newUsermealDto, String username);
}
