package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dao.MealDao;
import com.sda.caloriecounterbackend.dao.MealProductDao;
import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.dto.MealDto;
import com.sda.caloriecounterbackend.dto.MealWithProductsDto;
import com.sda.caloriecounterbackend.dto.UserProductDto;
import com.sda.caloriecounterbackend.entities.Meal;
import com.sda.caloriecounterbackend.entities.MealProduct;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.entities.User;
import com.sda.caloriecounterbackend.mapper.MealMapper;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.impl.MealServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceImplTest {
    @InjectMocks
    private MealServiceImpl mealService;
    @Mock
    private MealDao mealDao;
    @Mock
    private MealProductDao mealProductDao;
    @Mock
    private MealMapper mealMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ResponseUriBuilder responseUriBuilder;
    @Mock
    private UserDao userDao;
    @Mock
    private ProductDao productDao;

    @Test
    void shouldDefaultAndUserMeals() {
        //given
        when(mealDao.getUserAndDefault(anyString(), anyInt(), anyInt())).thenReturn(new PageImpl<>(Collections.singletonList(new Meal())));
        when(mealMapper.mapToDto(any())).thenReturn(new MealDto());
        //when
        ResponseEntity<Page<MealDto>> result = mealService.getDefaultAndUserMeals("TEST", 0, 10);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isEmpty());
    }
    @Test
    void shouldGetMealById() {
        //given
        Meal meal = new Meal();
        Product product = new Product();
        product.setCalories(200D);
        product.setCarbohydrates(10D);
        product.setFat(10D);
        product.setWhey(10D);
        MealProduct mealProduct = new MealProduct();
        mealProduct.setWeight(20D);
        mealProduct.setProduct(product);
        meal.setMealProducts(Collections.singletonList(mealProduct));
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(meal));
        when(mealMapper.mapToDto(any())).thenReturn(new MealDto());
        when(productMapper.mapToUserProductDto(any(), anyDouble())).thenReturn(new UserProductDto());
        //when
        ResponseEntity<MealWithProductsDto> result = mealService.getMealById(1L);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }
    @Test
    void shouldNotGetMealByIdIfNotExist() {
        //given
        when(mealDao.getById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<MealWithProductsDto> result = mealService.getMealById(1L);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }
    @Test
    void shouldSaveMeal() throws URISyntaxException {
        //given
        Meal saveResultExpected = new Meal();
        saveResultExpected.setId(1L);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(mealMapper.mapToDb(any())).thenReturn(new Meal());
        when(mealDao.save(any())).thenReturn(saveResultExpected);
        when(responseUriBuilder.buildUri(anyLong())).thenReturn(new URI("http://localhost:8080"));
        //when
        ResponseEntity<?> result = mealService.saveMeal("TEST", new MealDto());
        //then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getHeaders().containsKey("Location"));
    }
    @Test
    void shouldNotSaveMealIfUserNotFound() {
        //given
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = mealService.saveMeal("TEST", new MealDto());
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldEditMeal() {
        //given
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(new Meal()));
        doNothing().when(mealDao).modify(any());
        //when
        ResponseEntity<?> result = mealService.editMeal(1L, new MealDto());
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotEditMealWhenNotFound() {
        //given
        when(mealDao.getById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = mealService.editMeal(1L, new MealDto());
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verifyNoMoreInteractions(mealDao);
    }
    @Test
    void shouldDeleteMeal() {
        //given
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(new Meal()));
        doNothing().when(mealDao).delete(any());
        //when
        ResponseEntity<?> result = mealService.deleteMeal(1L);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotDeleteMealWhenNotFound() {
        //given
        when(mealDao.getById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = mealService.deleteMeal(1L);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verifyNoMoreInteractions(mealDao);
    }
    @Test
    void shouldAddProductToMealIfNotContain() {
        //given
        Product product = new Product();
        product.setWhey(10D);
        product.setFat(10D);
        product.setCarbohydrates(10D);
        product.setCalories(200D);
        MealProduct mealProduct = new MealProduct();
        mealProduct.setProduct(product);
        mealProduct.setWeight(100D);
        Meal meal = new Meal();
        List<MealProduct> mealProducts = new ArrayList<>();
        mealProducts.add(mealProduct);
        meal.setMealProducts(mealProducts);
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(productDao.findById(anyLong())).thenReturn(Optional.of(product));
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(meal));
        doNothing().when(mealProductDao).save(any());
        doNothing().when(mealDao).modify(any());
        //when
        ResponseEntity<?> result = mealService.addProductToMeal(1L, 1L, 10D);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldAddProductToMealModifyProductInMealIfAlreadyContains() {
        //given
        Product product = new Product();
        product.setWhey(10D);
        product.setFat(10D);
        product.setCarbohydrates(10D);
        product.setCalories(200D);
        MealProduct mealProduct = new MealProduct();
        mealProduct.setProduct(product);
        mealProduct.setWeight(100D);
        Meal meal = new Meal();
        List<MealProduct> mealProducts = new ArrayList<>();
        mealProducts.add(mealProduct);
        meal.setMealProducts(mealProducts);
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.of(mealProduct));
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(meal));
        doNothing().when(mealProductDao).modify(any());
        doNothing().when(mealDao).modify(any());
        //when
        ResponseEntity<?> result = mealService.addProductToMeal(1L, 1L, 10D);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNoAddProductToMealIfWeightIs0() {
        //when
        //when
        ResponseEntity<?> result = mealService.addProductToMeal(1L, 1L, 0D);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verifyNoInteractions(mealProductDao, mealDao, productDao);
    }
    @Test
    void shouldNotAddProductToMealIfProductNotFound() {
        //given
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(productDao.findById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = mealService.addProductToMeal(1L, 1L, 10D);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldNotAddProductToMealIfMealNotFound() {
        //given
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(productDao.findById(anyLong())).thenReturn(Optional.of(new Product()));
        when(mealDao.getById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = mealService.addProductToMeal(1L, 1L, 10D);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldDeleteProductFromMeal() {
        //given
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.of(new MealProduct()));
        doNothing().when(mealProductDao).delete(any());
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(new Meal()));
        doNothing().when(mealDao).modify(any());
        //when
        ResponseEntity<?> result = mealService.deleteProductFromMeal(1L, 1L);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotDeleteProductFromMealIfNotFound() {
        //given
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = mealService.deleteProductFromMeal(1L, 1L);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldModifyProductWeightInMeal() {
        //given
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.of(new MealProduct()));
        doNothing().when(mealProductDao).modify(any());
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(new Meal()));
        doNothing().when(mealDao).modify(any());
        //when
        ResponseEntity<?> result = mealService.modifyProductWeightInMeal(1L, 1L, 10D);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldModifyProductWeightInMealDeleteMealIfWeightIs0() {
        //given
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.of(new MealProduct()));
        doNothing().when(mealProductDao).delete(any());
        when(mealDao.getById(anyLong())).thenReturn(Optional.of(new Meal()));
        doNothing().when(mealDao).modify(any());
        //when
        ResponseEntity<?> result = mealService.modifyProductWeightInMeal(1L, 1L, 0D);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotModifyProductWeightInMealIfNotFound() {
        //given
        when(mealProductDao.getByProductIdAndMealId(anyLong(), anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = mealService.modifyProductWeightInMeal(1L, 1L, 10D);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

}