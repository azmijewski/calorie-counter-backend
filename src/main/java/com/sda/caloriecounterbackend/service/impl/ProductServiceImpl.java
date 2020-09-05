package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dao.ProductSearchDao;
import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.exception.ProductNotFoundException;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.ProductService;
import com.sda.caloriecounterbackend.util.ResponseUriBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Objects;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    private static final String SEPARATOR = "\\s+";

    private final ProductDao productDao;
    private final ProductMapper productMapper;
    private final ResponseUriBuilder responseUriBuilder;
    private final ProductSearchDao productSearchDao;

    public ProductServiceImpl(ProductDao productDao, ProductMapper productMapper, ResponseUriBuilder responseUriBuilder, ProductSearchDao productSearchDao) {
        this.productDao = productDao;
        this.productMapper = productMapper;
        this.responseUriBuilder = responseUriBuilder;
        this.productSearchDao = productSearchDao;
    }

    @Override
    public ResponseEntity<ProductDto> getById(Long id) {
        log.info("Get products by id: {}", id);
        ProductDto productDto;
        try {
            Product product = productDao.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
            productDto = productMapper.mapToDto(product);
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDto);

    }

    @Override
    public ResponseEntity<Page<ProductDto>> getProductsPage(int page, int size) {
        log.info("Get products page");
        Page<ProductDto> products = productDao.findAll(page, size)
                .map(productMapper::mapToDto);
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<Page<ProductDto>> searchProducts(int page, int size, String search) {
        log.info("Search products by: {}", search);
        Page<ProductDto> products;
        try{
            String[] searchWords = getSearchWords(search);
            products = productSearchDao.findByCriteria(searchWords, page, size)
                    .map(productMapper::mapToDto);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<?> save(ProductDto productDto) {
        log.info("Saving new product");
        Product productToSave = productMapper.mapToDb(productDto);
        Long resultId = productDao.save(productToSave)
                .getId();
        URI location = responseUriBuilder.buildUri(resultId);
        return ResponseEntity.created(location).build();
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        log.info("Delete id: {}", id);
        try {
            Product productToDelete = productDao.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
            productDao.delete(productToDelete);
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> modify(Long id, ProductDto productDto) {
        log.info("Modify by id: {}", id);
        try {
            Product productToModify = productDao.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
            productMapper.mapDataToUpdate(productToModify, productDto);
            productDao.update(productToModify);
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    private String[] getSearchWords(String search) {
        String[] wordsToSearch = StringUtils.split(search, SEPARATOR);
        if(Objects.isNull(wordsToSearch)) {
            wordsToSearch = new String[] {search};
        }
        validateSearchWords(wordsToSearch);
        return wordsToSearch;
    }
    private void validateSearchWords(String[] searchWords) {
        for (String searchWord : searchWords) {
            if (searchWord.matches(SEPARATOR)) {
                throw new IllegalArgumentException("Search word: " + searchWord + " is invalid");
            }
        }
    }


}
