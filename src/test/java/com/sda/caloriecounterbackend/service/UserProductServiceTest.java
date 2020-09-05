package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.dao.UserProductDao;
import com.sda.caloriecounterbackend.dto.*;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.entities.User;
import com.sda.caloriecounterbackend.entities.UserProduct;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.impl.UserProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProductServiceTest {
    @InjectMocks
    private UserProductServiceImpl userProductService;
    @Mock
    private UserProductDao userProductDao;
    @Mock
    private UserDao userDao;
    @Mock
    private ProductDao productDao;
    @Mock
    private ProductMapper productMapper;

    @Test
    void shouldAddUserProductIfNotExist() {
        //given
        NewUserProductDto userProductDto = new NewUserProductDto();
        userProductDto.setDate(LocalDate.now());
        userProductDto.setProductId(1L);
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong())).thenReturn(Optional.empty());
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(productDao.findById(anyLong())).thenReturn(Optional.of(new Product()));
        when(userProductDao.save(any())).thenReturn(new UserProduct());
        //when
        ResponseEntity<?> result = userProductService.addProduct(userProductDto, "test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotAddUserProductIfNotExistAndUserNotFound() {
        //given
        NewUserProductDto userProductDto = new NewUserProductDto();
        userProductDto.setDate(LocalDate.now());
        userProductDto.setProductId(1L);
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong())).thenReturn(Optional.empty());
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userProductService.addProduct(userProductDto, "test");
        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
    @Test
    void shouldNotAddUserProductIfNotExistAndProductNotFound() {
        //given
        NewUserProductDto userProductDto = new NewUserProductDto();
        userProductDto.setDate(LocalDate.now());
        userProductDto.setProductId(1L);
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong())).thenReturn(Optional.empty());
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(productDao.findById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userProductService.addProduct(userProductDto, "test");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldAddUserProductModifyIfProductExist() {
        //given
        NewUserProductDto userProductDto = new NewUserProductDto();
        userProductDto.setDate(LocalDate.now());
        userProductDto.setProductId(1L);
        userProductDto.setWeight(100D);
        UserProduct userProduct = new UserProduct();
        userProduct.setWeight(100D);
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong())).thenReturn(Optional.of(userProduct));
        doNothing().when(userProductDao).modify(any());
        //when
        ResponseEntity<?> result = userProductService.addProduct(userProductDto, "test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldGetAllByDate() {
        //given
        Product product = new Product();
        product.setCalories(100D);
        product.setWhey(10D);
        product.setFat(10D);
        product.setCarbohydrates(10D);
        UserProduct userProduct = new UserProduct();
        userProduct.setWeight(100D);
        userProduct.setProduct(product);
        User user = new User();
        user.setCalorie(2500D);
        when(userProductDao.findAllByDateAndUsername(any(), anyString()))
                .thenReturn(Collections.singletonList(userProduct));
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(productMapper.mapToUserProductDto(any(), anyDouble())).thenReturn(new UserProductDto());
        //when
        ResponseEntity<UserProductsListDto> result = userProductService.getAllByDate(LocalDate.now(), "test");
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }
    @Test
    void shouldNotGetAllByDateWhenUserNotFound() {
        //given
        UserProduct userProduct = new UserProduct();
        userProduct.setWeight(100D);
        when(userProductDao.findAllByDateAndUsername(any(), anyString()))
                .thenReturn(Collections.singletonList(userProduct));
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<UserProductsListDto> result = userProductService.getAllByDate(LocalDate.now(), "test");
        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }
    @Test
    void shouldRemoveIfExist() {
        //given
        DeleteUserProductDto deleteUserProductDto = new DeleteUserProductDto();
        deleteUserProductDto.setProductId(1L);
        deleteUserProductDto.setDate(LocalDate.now());
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong()))
                .thenReturn(Optional.of(new UserProduct()));
        doNothing().when(userProductDao).remove(any());
        //when
        ResponseEntity<?> result = userProductService.removeProduct(deleteUserProductDto, "test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotRemoveIfNotExist() {
        //given
        DeleteUserProductDto deleteUserProductDto = new DeleteUserProductDto();
        deleteUserProductDto.setProductId(1L);
        deleteUserProductDto.setDate(LocalDate.now());
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong()))
                .thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userProductService.removeProduct(deleteUserProductDto, "test");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldModifyUserProductWhenWeightIsNotEqualsThen0() {
        //given
        ModifyUserProductDto modifyUserProductDto = new ModifyUserProductDto();
        modifyUserProductDto.setDate(LocalDate.now());
        modifyUserProductDto.setNewWeight(100D);
        modifyUserProductDto.setProductId(1L);
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong()))
                .thenReturn(Optional.of(new UserProduct()));
        doNothing().when(userProductDao).modify(any());
        //when
        ResponseEntity<?> result = userProductService.modifyUserProduct(modifyUserProductDto, "test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

    }
    @Test
    void shouldNotModifyUserProductWhenUserProductNotFound() {
        //given
        ModifyUserProductDto modifyUserProductDto = new ModifyUserProductDto();
        modifyUserProductDto.setDate(LocalDate.now());
        modifyUserProductDto.setNewWeight(100D);
        modifyUserProductDto.setProductId(1L);
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong()))
                .thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userProductService.modifyUserProduct(modifyUserProductDto, "test");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldDeleteWhenWeightIsEqualsTo0() {
        //given
        ModifyUserProductDto modifyUserProductDto = new ModifyUserProductDto();
        modifyUserProductDto.setDate(LocalDate.now());
        modifyUserProductDto.setNewWeight(0D);
        modifyUserProductDto.setProductId(1L);
        when(userProductDao.findByUsernameAndDateAndProductId(anyString(), any(), anyLong()))
                .thenReturn(Optional.of(new UserProduct()));
        doNothing().when(userProductDao).remove(any());
        //when
        ResponseEntity<?> result = userProductService.modifyUserProduct(modifyUserProductDto, "test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }


}