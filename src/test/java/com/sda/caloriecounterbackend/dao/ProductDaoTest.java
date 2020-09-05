package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.dao.impl.ProductDaoImpl;
import com.sda.caloriecounterbackend.entities.Article;
import com.sda.caloriecounterbackend.entities.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductDaoTest {
    @InjectMocks
    private ProductDaoImpl productDao;
    @Mock
    private EntityManager em;
    @Mock
    private Query query;

    @Test
    void shouldReturnEmptyPage() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<Article>());
        //when
        Page<Product> result = productDao.findAll(0, 10);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldReturnNotEmptyPage() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(new Product()));
        //when
        Page<Product> result = productDao.findAll(0, 10);
        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldReturnArticleIfExist() {
        //given
        when(em.find(any(), anyLong())).thenReturn(new Product());
        //when
        Optional<Product> result = productDao.findById(1L);
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldNoReturnArticleIfNotExist() {
        //given
        when(em.find(any(), anyLong())).thenReturn(null);
        //when
        Optional<Product> result = productDao.findById(1L);
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldSave() {
        //given
        doNothing().when(em).persist(any());
        //when
        productDao.save(new Product());
        //then
        verify(em).persist(any());
    }
    @Test
    void shouldUpdate() {
        //given
        when(em.merge(any())).thenReturn(new Product());
        //when
        productDao.update(new Product());
        //then
        verify(em).merge(any());
    }
    @Test
    void shouldDelete() {
        //given
        doNothing().when(em).remove(any());
        //when
        productDao.delete(new Product());
        //then
        verify(em).remove(any());
    }

}