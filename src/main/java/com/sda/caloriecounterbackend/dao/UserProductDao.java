package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.UserProduct;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserProductDao {
    List<UserProduct> findAllByDateAndUsername(LocalDate localDate, String username);
    UserProduct save(UserProduct userProduct);
    Optional<UserProduct> findByUsernameAndDateAndProductId(String username, LocalDate date, Long productId);
    void modify(UserProduct userProduct);
    void remove(UserProduct userProduct);
}
