package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.NewUserProductDto;
import com.sda.caloriecounterbackend.dto.UserProductsListDto;
import com.sda.caloriecounterbackend.service.UserProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class UserProductController {

    private final UserProductService userProductService;

    public UserProductController(UserProductService userProductService) {
        this.userProductService = userProductService;
    }
    @GetMapping("/calories")
    public ResponseEntity<UserProductsListDto> getCaloriesForDate(Principal principal,
                                                                  @RequestParam(name = "date")LocalDate date) {
        return userProductService.getAllByDate(date, principal.getName());
    }
    @PostMapping("/calories")
    public ResponseEntity<?> addCalorie(Principal principal,
                                        @RequestBody @Valid NewUserProductDto newUserProductDto) {
        return userProductService.addProduct(newUserProductDto, principal.getName());
    }
}
