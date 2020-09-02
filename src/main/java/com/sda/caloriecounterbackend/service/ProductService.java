package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public interface ProductService {
    ResponseEntity<ProductDto> getById(Long id);
    ResponseEntity<Page<ProductDto>> getProductsPage(int page, int size);
    ResponseEntity<Page<ProductDto>> searchProducts(int page, int size, String searchWords);
    ResponseEntity<?> save(ProductDto productDto);
    ResponseEntity<?> delete(Long id);
    ResponseEntity<?> modify(Long id, ProductDto productDto);
}
