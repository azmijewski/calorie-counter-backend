package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.NewUserProductDto;
import com.sda.caloriecounterbackend.dto.UserProductsListDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface UserProductService {
    ResponseEntity<?> addProduct(NewUserProductDto userProductDto, String username);
    ResponseEntity<UserProductsListDto> getAllByDate(LocalDate date, String username);
}
