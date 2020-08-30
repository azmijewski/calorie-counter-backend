package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductDao {

    Optional<Product> findById(Long id);
    Page<Product> findAll(int page, int size);
    Product save(Product product);
    void update(Product product);
    void delete(Product product);
}
