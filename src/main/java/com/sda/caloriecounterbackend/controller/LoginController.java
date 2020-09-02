package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.LoginDto;
import com.sda.caloriecounterbackend.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        return loginService.login(loginDto.getLogin(), loginDto.getPassword());
    }
}
