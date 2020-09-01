package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.NewUserProductDto;
import com.sda.caloriecounterbackend.dto.UserProductsListDto;

import java.time.LocalDate;

public interface UserProductService {
    void addProduct(NewUserProductDto userProductDto, String username);
    UserProductsListDto getAllByDate(LocalDate date, String username);
}
