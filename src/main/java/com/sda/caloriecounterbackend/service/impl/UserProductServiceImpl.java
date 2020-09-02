package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.dao.UserProductDao;
import com.sda.caloriecounterbackend.dto.NewUserProductDto;
import com.sda.caloriecounterbackend.dto.UserProductDto;
import com.sda.caloriecounterbackend.dto.UserProductsListDto;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.entities.User;
import com.sda.caloriecounterbackend.entities.UserProduct;
import com.sda.caloriecounterbackend.exception.ProductNotFoundException;
import com.sda.caloriecounterbackend.exception.UserNotFoundException;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.UserProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class UserProductServiceImpl implements UserProductService {

    private final UserProductDao userProductDao;
    private final UserDao userDao;
    private final ProductDao productDao;
    private final ProductMapper productMapper;

    public UserProductServiceImpl(UserProductDao userProductDao, UserDao userDao, ProductDao productDao, ProductMapper productMapper) {
        this.userProductDao = userProductDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.productMapper = productMapper;
    }

    @Override
    public ResponseEntity<?> addProduct(NewUserProductDto userProductDto, String username) {
        try {
            User user = userDao.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));
            Product product = productDao.findById(userProductDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + userProductDto.getProductId()));
            UserProduct userProduct = new UserProduct();
            userProduct.setDate(userProductDto.getDate());
            userProduct.setWeight(userProductDto.getWeight());
            userProduct.setProduct(product);
            userProduct.setUser(user);
            userProductDao.save(userProduct);
        } catch (UserNotFoundException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ProductNotFoundException e) {
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<UserProductsListDto> getAllByDate(LocalDate date, String username) {
        UserProductsListDto userProductsListDto = new UserProductsListDto();
        try {
            List<UserProduct> userProducts = userProductDao.findAllByDateAndUsername(date, username);
            List<UserProductDto> products = mapProductsListWithCalculatedData(userProducts);
            Double totalCalories = calculateCalories(products);
            Double calorieGoal = userDao.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username))
                    .getCalorie();

            userProductsListDto.setCalorieGoal(calorieGoal);
            userProductsListDto.setTotalCalories(totalCalories);
            userProductsListDto.setDifference(calorieGoal - totalCalories);
            userProductsListDto.setProducts(products);
        } catch (UserNotFoundException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(userProductsListDto);
    }

    private List<UserProductDto> mapProductsListWithCalculatedData(List<UserProduct> userProducts) {
        List<UserProductDto> products = new ArrayList<>();
        for (UserProduct userProduct : userProducts) {
            Product product = userProduct.getProduct();
            UserProductDto productDto = productMapper.mapToUserProductDto(product, userProduct.getWeight());
            Double multiplier = userProduct.getWeight() / 100;
            productDto.setCalories(product.getCalories() * multiplier);
            productDto.setCarbohydrates(product.getCarbohydrates() * multiplier);
            productDto.setFat(product.getFat() * multiplier);
            productDto.setWhey(product.getWhey() * multiplier);
            products.add(productDto);
        }
        return products;
    }

    private Double calculateCalories(List<UserProductDto> products) {
        return products.stream()
                .map(UserProductDto::getCalories)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

}
