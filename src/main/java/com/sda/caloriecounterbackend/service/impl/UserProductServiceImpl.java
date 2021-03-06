package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.*;
import com.sda.caloriecounterbackend.dto.*;
import com.sda.caloriecounterbackend.entities.*;
import com.sda.caloriecounterbackend.exception.MealNotFoundException;
import com.sda.caloriecounterbackend.exception.ProductNotFoundException;
import com.sda.caloriecounterbackend.exception.UserNotFoundException;
import com.sda.caloriecounterbackend.exception.UserProductNotFoundException;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.UserProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserProductServiceImpl implements UserProductService {

    private final UserProductDao userProductDao;
    private final UserDao userDao;
    private final ProductDao productDao;
    private final ProductMapper productMapper;
    private final MealDao mealDao;

    public UserProductServiceImpl(UserProductDao userProductDao, UserDao userDao,
                                  ProductDao productDao, ProductMapper productMapper, MealDao mealDao) {
        this.userProductDao = userProductDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.productMapper = productMapper;
        this.mealDao = mealDao;
    }

    @Override
    public ResponseEntity<?> addProduct(NewUserProductDto userProductDto, String username) {
        log.info("Add new product");
        try {
            if(userProductDto.getWeight() == 0) {
                return ResponseEntity.noContent().build();
            }
            Optional<UserProduct> userProductInDb =
                    userProductDao.findByUsernameAndDateAndProductId(username, userProductDto.getDate(), userProductDto.getProductId());
            if (userProductInDb.isPresent()) {
                editUserProduct(userProductInDb.get(), userProductDto.getWeight());
            } else {
                addNewProduct(userProductDto, username);
            }
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<UserProductsListDto> getAllByDate(LocalDate date, String username) {
        log.info("Getting user products from date");
        UserProductsListDto userProductsListDto = new UserProductsListDto();
        try {
            List<UserProduct> userProducts = userProductDao.findAllByDateAndUsername(date, username);
            Double calorieGoal = userDao.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username))
                    .getCalorie();
            List<UserProductDto> products = mapProductsListWithCalculatedData(userProducts);
            Double totalCalories = calculateCalories(products);
            userProductsListDto.setCalorieGoal(calorieGoal);
            userProductsListDto.setTotalCalories(totalCalories);
            userProductsListDto.setDifference(calorieGoal - totalCalories);
            userProductsListDto.setProducts(products);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(userProductsListDto);
    }

    @Override
    public ResponseEntity<?> removeProduct(DeleteUserProductDto deleteUserProductDto, String username) {
        log.info("Remove products");
        try {
            UserProduct userProduct
                    = userProductDao.findByUsernameAndDateAndProductId(username, deleteUserProductDto.getDate(), deleteUserProductDto.getProductId())
                    .orElseThrow(() -> new UserProductNotFoundException("Could not find user-product"));
            userProductDao.remove(userProduct);
        } catch (UserProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> modifyUserProduct(ModifyUserProductDto modifyUserProductDto, String username) {
        log.info("Modify products by user");
        if (modifyUserProductDto.getNewWeight() == 0) {
            DeleteUserProductDto deleteUserProductDto = new DeleteUserProductDto();
            deleteUserProductDto.setDate(modifyUserProductDto.getDate());
            deleteUserProductDto.setProductId(modifyUserProductDto.getProductId());
            return removeProduct(deleteUserProductDto, username);
        }
        try {
            UserProduct userProduct
                    = userProductDao.findByUsernameAndDateAndProductId(username, modifyUserProductDto.getDate(), modifyUserProductDto.getProductId())
                    .orElseThrow(() -> new UserProductNotFoundException("Could not find user-product"));
            userProduct.setWeight(modifyUserProductDto.getNewWeight());
            userProductDao.modify(userProduct);
        } catch (UserProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> addMeal(NewUserMealDto newUsermealDto, String username) {
        try {
            User user = userDao.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));
            Meal meal = mealDao.getById(newUsermealDto.getMealId())
                    .orElseThrow(() -> new MealNotFoundException("Could not find meal with id: " + newUsermealDto.getMealId()));
            meal.getMealProducts().forEach( mealProduct -> {
                Optional<UserProduct> userProductInDb =
                        userProductDao.findByUsernameAndDateAndProductId(username, newUsermealDto.getDate(), mealProduct.getProduct().getId());
                if (userProductInDb.isPresent()) {
                    editUserProduct(userProductInDb.get(), mealProduct.getWeight());
                } else {
                    UserProduct userProduct = new UserProduct();
                    userProduct.setProduct(mealProduct.getProduct());
                    userProduct.setWeight(mealProduct.getWeight());
                    userProduct.setUser(user);
                    userProduct.getId().setDate(newUsermealDto.getDate());
                    userProductDao.save(userProduct);
                }
            });
        } catch (UserNotFoundException | MealNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    private void editUserProduct(UserProduct userProduct, Double weight) {
        userProduct.setWeight(userProduct.getWeight() + weight);
        userProductDao.modify(userProduct);
    }

    private void addNewProduct(NewUserProductDto userProductDto, String username) {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));
        Product product = productDao.findById(userProductDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Could not find product with id: " + userProductDto.getProductId()));
        UserProduct userProduct = new UserProduct();
        userProduct.getId().setDate(userProductDto.getDate());
        userProduct.setWeight(userProductDto.getWeight());
        userProduct.setProduct(product);
        userProduct.setUser(user);
        userProductDao.save(userProduct);
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
