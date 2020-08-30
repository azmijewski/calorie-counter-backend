package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.UserProduct;

import java.time.LocalDate;
import java.util.List;

public interface UserProductDao {
    List<UserProduct> findAllByDate(LocalDate localDate, String username);
    UserProduct save(UserProduct userProduct);
}
