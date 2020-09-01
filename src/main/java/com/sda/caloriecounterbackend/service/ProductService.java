package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.ProductDto;
import org.springframework.data.domain.Page;

import java.net.URI;

public interface ProductService {
    ProductDto getById(Long id);
    Page<ProductDto> getProductsPage(int page, int size);
    Long save(ProductDto productDto);
    void delete(Long id);
    void modify(Long id, ProductDto productDto);
}
