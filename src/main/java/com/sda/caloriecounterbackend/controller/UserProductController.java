package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.*;
import com.sda.caloriecounterbackend.service.UserProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("api/v1/calories")
@CrossOrigin(origins = "*")
public class UserProductController {

    private final UserProductService userProductService;

    public UserProductController(UserProductService userProductService) {
        this.userProductService = userProductService;
    }
    @GetMapping
    public ResponseEntity<UserProductsListDto> getCaloriesForDate(Principal principal,
                                                                  @RequestParam(name = "date") String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyy-MM-dd"));
        return userProductService.getAllByDate(localDate, principal.getName());
    }
    @PostMapping
    public ResponseEntity<?> addCalorie(Principal principal,
                                        @RequestBody @Valid NewUserProductDto newUserProductDto) {
        return userProductService.addProduct(newUserProductDto, principal.getName());
    }
    @PostMapping("/remove")
    public ResponseEntity<?> deleteUserProduct(Principal principal,
                                               @RequestBody @Valid DeleteUserProductDto deleteUserProductDto) {
        return userProductService.removeProduct(deleteUserProductDto, principal.getName());
    }
    @PutMapping
    public ResponseEntity<?> editUserProductWeight(Principal principal,
                                                   @RequestBody @Valid ModifyUserProductDto modifyUserProductDto) {
        return userProductService.modifyUserProduct(modifyUserProductDto, principal.getName());
    }
    @PostMapping("/meals")
    public ResponseEntity<?> addMeal(Principal principal,
                                     @RequestBody @Valid NewUserMealDto userMealDto) {
        return userProductService.addMeal(userMealDto, principal.getName());
    }
}
