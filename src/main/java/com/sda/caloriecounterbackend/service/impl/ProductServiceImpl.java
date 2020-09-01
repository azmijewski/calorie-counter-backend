package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.exception.ProductNotFoundException;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductDao productDao, ProductMapper productMapper) {
        this.productDao = productDao;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDto getById(Long id) {
        Product product = productDao.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
        return productMapper.mapToDto(product);
    }

    @Override
    public Page<ProductDto> getProductsPage(int page, int size) {
        return productDao.findAll(page, size)
                .map(productMapper::mapToDto);
    }

    @Override
    public Long save(ProductDto productDto) {
        Product productToSave = productMapper.mapToDb(productDto);
        return productDao.save(productToSave)
                .getId();
    }

    @Override
    public void delete(Long id) {
        Product productToDelete = productDao.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
        productDao.delete(productToDelete);
    }

    @Override
    public void modify(Long id, ProductDto productDto) {
        Product productToModify = productDao.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + id));
        productMapper.mapDataToUpdate(productToModify, productDto);
        productDao.update(productToModify);
    }


}
