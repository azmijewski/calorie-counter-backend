package com.sda.caloriecounterbackend.service;

import org.springframework.http.ResponseEntity;

public interface LoginService {
    ResponseEntity<?> login(String username, String password);
}
