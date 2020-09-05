package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.Product;
import org.springframework.data.domain.Page;

public interface ProductSearchDao {
    Page<Product> findByCriteria(String[] search, int page, int size);
}
