package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.MealDao;
import com.sda.caloriecounterbackend.dao.MealProductDao;
import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.dto.MealDto;
import com.sda.caloriecounterbackend.dto.MealWithProductsDto;
import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.dto.UserProductDto;
import com.sda.caloriecounterbackend.entities.Meal;
import com.sda.caloriecounterbackend.entities.MealProduct;
import com.sda.caloriecounterbackend.entities.Product;
import com.sda.caloriecounterbackend.entities.User;
import com.sda.caloriecounterbackend.exception.*;
import com.sda.caloriecounterbackend.mapper.MealMapper;
import com.sda.caloriecounterbackend.mapper.ProductMapper;
import com.sda.caloriecounterbackend.service.MealService;
import com.sda.caloriecounterbackend.util.ResponseUriBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class MealServiceImpl implements MealService {

    private final MealDao mealDao;
    private final MealProductDao mealProductDao;
    private final MealMapper mealMapper;
    private final ProductMapper productMapper;
    private final ResponseUriBuilder responseUriBuilder;
    private final UserDao userDao;
    private final ProductDao productDao;

    public MealServiceImpl(MealDao mealDao,
                           MealProductDao mealProductDao,
                           MealMapper mealMapper,
                           ProductMapper productMapper,
                           ResponseUriBuilder responseUriBuilder,
                           UserDao userDao, ProductDao productDao) {
        this.mealDao = mealDao;
        this.mealProductDao = mealProductDao;
        this.mealMapper = mealMapper;
        this.productMapper = productMapper;
        this.responseUriBuilder = responseUriBuilder;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @Override
    public ResponseEntity<Page<MealDto>> getDefaultAndUserProducts(String username, int page, int size) {
        Page<MealDto> result = mealDao.getUserAndDefault(username, page, size)
                .map(mealMapper::mapToDto);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<MealWithProductsDto> getMealById(Long mealId) {
        MealWithProductsDto result;
        try {
            Meal meal = mealDao.getById(mealId)
                    .orElseThrow(() -> new MealNotFoundException("Could not find meal with id: " + mealId));
            MealDto mealDto = mealMapper.mapToDto(meal);
            result = mapProductsListWithCalculatedData(meal.getMealProducts(), mealDto);
        } catch (MealNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> saveMeal(String username, MealDto mealDto) {
        Long resultId;
        try {
            User user = userDao.findByUsername(username)
                    .orElseThrow(() -> new UserProductNotFoundException("Could not find user: " + username));
            Meal meal = mealMapper.mapToDb(mealDto);
            meal.setIsDefault(false);
            meal.setUser(user);
            resultId = mealDao.save(meal).getId();
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        URI location = responseUriBuilder.buildUri(resultId);
        return ResponseEntity.created(location).build();
    }

    @Override
    public ResponseEntity<?> editMeal(Long mealId, MealDto mealDto) {
        try {
            Meal meal = mealDao.getById(mealId)
                    .orElseThrow(() -> new MealNotFoundException("Could not find meal with id: " + mealId));
            meal.setName(mealDto.getName());
            mealDao.modify(meal);
        } catch (MealNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<?> deleteMeal(Long mealId) {
        try {
            Meal meal = mealDao.getById(mealId)
                    .orElseThrow(() -> new MealNotFoundException("Could not find meal with id: " + mealId));
            mealDao.delete(meal);
        } catch (MealNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> addProductToMeal(Long productId, Long mealId, Double weight) {
        try {
            Optional<MealProduct> optionalMealProduct = mealProductDao.getByProductIdAndMealId(productId, mealId);
            if (optionalMealProduct.isPresent()) {
                MealProduct mealProduct = optionalMealProduct.get();
                mealProduct.setWeight(mealProduct.getWeight() + weight);
                mealProductDao.modify(mealProduct);
            } else {
                Product product = productDao.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Cloud not find product with id: " + productId));
                Meal meal = mealDao.getById(mealId)
                        .orElseThrow(() -> new MealNotFoundException("Could not find meal with id: " + mealId));
                MealProduct mealProduct = new MealProduct();
                mealProduct.setWeight(weight);
                mealProduct.setMeal(meal);
                mealProduct.setProduct(product);
                meal.getMealProducts().add(mealProduct);
                mealProductDao.save(mealProduct);
            }
            updateMealMacro(mealId);
        } catch (ProductNotFoundException | MealNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<?> deleteProductFromMeal(Long productId, Long mealId) {
        try {
            MealProduct mealProduct = mealProductDao.getByProductIdAndMealId(productId, mealId)
                    .orElseThrow(() -> new MealProductNotFoundException("Could not find product with id: "
                            + productId + " in meal with id: " + mealId));
            mealProductDao.delete(mealProduct);
            updateMealMacro(mealId);
        } catch (MealProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> modifyProductWeightInMeal(Long productId, Long mealId, Double newWeight) {
        if (newWeight == 0) {
            return deleteProductFromMeal(productId, mealId);
        }
        try {
            MealProduct mealProduct = mealProductDao.getByProductIdAndMealId(productId, mealId)
                    .orElseThrow(() -> new MealProductNotFoundException("Could not find product with id: "
                            + productId + " in meal with id: " + mealId));
            mealProduct.setWeight(newWeight);
            mealProductDao.modify(mealProduct);
            updateMealMacro(mealId);
        } catch (MealProductNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private void updateMealMacro(Long mealId) {
        Meal mealToUpdate = mealDao.getById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Could not find meal with id: " + mealId));
        double totalCalories = 0D;
        double totalWhey = 0D;
        double totalFat = 0D;
        double totalCarbs = 0D;
        for (MealProduct mealProduct : mealToUpdate.getMealProducts()) {
            Double multiplier = mealProduct.getWeight() / 100;
            Product product = mealProduct.getProduct();
            totalCalories += product.getCalories() * multiplier;
            totalWhey += product.getWhey() * multiplier;
            totalFat += product.getFat() * multiplier;
            totalCarbs += product.getCarbohydrates() * multiplier;
        }
        mealToUpdate.setCalories(totalCalories);
        mealToUpdate.setCarbohydrates(totalCarbs);
        mealToUpdate.setFat(totalFat);
        mealToUpdate.setWhey(totalWhey);
        mealDao.modify(mealToUpdate);
    }

    private MealWithProductsDto mapProductsListWithCalculatedData(List<MealProduct> mealProductList, MealDto meal) {
        MealWithProductsDto result = new MealWithProductsDto();
        List<UserProductDto> products = new ArrayList<>();
        for (MealProduct mealProduct : mealProductList) {
            Double multiplier = mealProduct.getWeight() / 100;
            Product product = mealProduct.getProduct();
            product.setCalories(product.getCalories() * multiplier);
            product.setCarbohydrates(product.getCarbohydrates() * multiplier);
            product.setFat(product.getFat() * multiplier);
            product.setWhey(product.getWhey() * multiplier);
            products.add(productMapper.mapToUserProductDto(product, mealProduct.getWeight()));
        }
        result.setMealDto(meal);
        result.setProducts(products);
        return result;
    }
}
