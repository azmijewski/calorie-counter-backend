package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.*;
import com.sda.caloriecounterbackend.service.MealService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("api/v1/meals")
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    public ResponseEntity<Page<MealDto>> getMeals(Principal principal,
                                                  @RequestParam(name = "page") int page,
                                                  @RequestParam(name = "size") int size) {
        return mealService.getDefaultAndUserProducts(principal.getName(), page, size);
    }
    @GetMapping("/{mealId}")
    public ResponseEntity<MealWithProductsDto> getMealById(@PathVariable(name = "mealId") Long mealId) {
        return mealService.getMealById(mealId);
    }
    @PostMapping
    public ResponseEntity<?> saveMeal(Principal principal, @RequestBody @Valid MealDto mealDto) {
        return mealService.saveMeal(principal.getName(), mealDto);
    }
    @PutMapping("/{mealId}")
    public ResponseEntity<?> editMeal(@PathVariable(name = "mealId") Long mealId,
                                      @RequestBody @Valid MealDto mealDto) {
        return mealService.editMeal(mealId, mealDto);
    }
    @DeleteMapping("/{mealId}")
    public ResponseEntity<?> deleteMeal(@PathVariable(name = "mealId") Long mealId) {
        return mealService.deleteMeal(mealId);
    }
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProductToMeal(@RequestBody @Valid NewProductMealDto newProductMealDto) {
        return mealService.addProductToMeal(newProductMealDto.getProductId(),
                newProductMealDto.getMealId(), newProductMealDto.getWeight());
    }
    @PostMapping("/deleteProduct")
    public ResponseEntity<?> deleteProductFromMeal(@RequestBody @Valid DeleteProductMealDto productMealDto) {
        return mealService.deleteProductFromMeal(productMealDto.getProductId(), productMealDto.getMealId());
    }
    @PutMapping("/modifyProduct")
    public ResponseEntity<?> modifyMealProduct(@RequestBody @Valid ModifyProductMealDto productMealDto) {
        return mealService.modifyProductWeightInMeal(productMealDto.getProductId(),
                productMealDto.getMealId(), productMealDto.getWeight());
    }

}
