package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.dao.impl.ProductSearchDaoImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ProductSearchDaoTest {
    @InjectMocks
    private ProductSearchDaoImpl productSearchDao;
    @Mock
    private EntityManager em;

}