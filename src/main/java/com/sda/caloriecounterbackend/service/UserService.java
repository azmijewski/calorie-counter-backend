package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.entities.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    ResponseEntity<UserDto> getUserByUsername(String username);
    UserDto findById(Long userId);
    List<UserDto> findAll();
    ResponseEntity<?> modify(UserDto user, String username);
    ResponseEntity<?> delete(String username, String password);
    ResponseEntity<?> register(UserDto user);
    ResponseEntity<?> confirm(String token);
    ResponseEntity<?> changePassword(String newPassword, String oldPassword, String username);

}
