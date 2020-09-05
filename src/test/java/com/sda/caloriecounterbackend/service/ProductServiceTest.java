package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dao.ProductSearchDao;
import com.sda.caloriecounterbackend.dto.ArticleDto;
import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.impl.ProductServiceImpl;
import com.sda.caloriecounterbackend.util.ResponseUriBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductDao productDao;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ResponseUriBuilder responseUriBuilder;
    @Mock
    private ProductSearchDao productSearchDao;

    @Test
    void shouldGetByIdIfExist() {
        //given
        when(productDao.findById(anyLong())).thenReturn(Optional.of(new Product()));
        when(productMapper.mapToDto(any())).thenReturn(new ProductDto());
        //when
        ResponseEntity<ProductDto> result = productService.getById(1L);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }
    @Test
    void shouldNotGetByIdIfNotExist() {
        //given
        when(productDao.findById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<ProductDto> result = productService.getById(1L);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }
    @Test
    void shouldGetProductPage() {
        //given
        when(productDao.findAll(anyInt(), anyInt())).thenReturn(new PageImpl<>(Collections.singletonList(new Product())));
        when(productMapper.mapToDto(any())).thenReturn(new ProductDto());
        //when
        ResponseEntity<Page<ProductDto>> result = productService.getProductsPage(0,10);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isEmpty());
    }
    @Test
    void shouldSearchProducts() {
        //given
        when(productSearchDao.findByCriteria(any(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Product())));
        when(productMapper.mapToDto(any())).thenReturn(new ProductDto());
        //when
        ResponseEntity<Page<ProductDto>> result = productService.searchProducts(0,10, "test");
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isEmpty());
    }
    @Test
    void shouldNotSearchIfInvalidSearchWord() {
        //given
        String invalidSearch = " ";
        //when
        ResponseEntity<Page<ProductDto>> result = productService.searchProducts(0,10, invalidSearch);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }
    @Test
    void shouldSave() throws URISyntaxException {
        //given
        when(productMapper.mapToDb(any())).thenReturn(new Product());
        when(productDao.save(any())).thenReturn(new Product(1L));
        when(responseUriBuilder.buildUri(anyLong())).thenReturn(new URI("http::/localhost:8080"));
        //when
        ResponseEntity<?> result = productService.save(new ProductDto());
        //then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getHeaders().containsKey("Location"));
    }
    @Test
    void shouldDeleteIfExist() {
        //given
        when(productDao.findById(anyLong())).thenReturn(Optional.of(new Product()));
        doNothing().when(productDao).delete(any());
        //when
        ResponseEntity<?> result = productService.delete(1L);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotDeleteIfNotExist() {
        //given
        when(productDao.findById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = productService.delete(1L);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verifyNoMoreInteractions(productDao);
    }
    @Test
    void shouldModify() {
        //given
        when(productDao.findById(anyLong())).thenReturn(Optional.of(new Product()));
        doNothing().when(productMapper).mapDataToUpdate(any(), any());
        doNothing().when(productDao).update(any());
        //when
        ResponseEntity<?> result = productService.modify(1L, new ProductDto());
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotModifyIfNotExist() {
        //given
        when(productDao.findById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = productService.modify(1L, new ProductDto());
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verifyNoInteractions(productMapper);
        verifyNoMoreInteractions(productDao);
    }

}