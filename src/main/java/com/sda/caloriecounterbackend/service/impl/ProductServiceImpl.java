package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.exception.ProductNotFoundException;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.ProductService;
import com.sda.caloriecounterbackend.util.ResponseUriBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final ProductMapper productMapper;
    private final ResponseUriBuilder responseUriBuilder;

    public ProductServiceImpl(ProductDao productDao, ProductMapper productMapper, ResponseUriBuilder responseUriBuilder) {
        this.productDao = productDao;
        this.productMapper = productMapper;
        this.responseUriBuilder = responseUriBuilder;
    }

    @Override
    public ResponseEntity<ProductDto> getById(Long id) {
        ProductDto productDto;
        try {
            Product product = productDao.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
            productDto = productMapper.mapToDto(product);
        } catch (ProductNotFoundException e) {
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDto);

    }

    @Override
    public ResponseEntity<Page<ProductDto>> getProductsPage(int page, int size) {
        Page<ProductDto> products = productDao.findAll(page, size)
                .map(productMapper::mapToDto);
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<?> save(ProductDto productDto) {
        Product productToSave = productMapper.mapToDb(productDto);
        Long resultId = productDao.save(productToSave)
                .getId();
        URI location = responseUriBuilder.buildUri(resultId);
        return ResponseEntity.created(location).build();
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            Product productToDelete = productDao.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
            productDao.delete(productToDelete);
        } catch (ProductNotFoundException e) {
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> modify(Long id, ProductDto productDto) {
        try {
            Product productToModify = productDao.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
            productMapper.mapDataToUpdate(productToModify, productDto);
            productDao.update(productToModify);
        } catch (ProductNotFoundException e) {
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


}
