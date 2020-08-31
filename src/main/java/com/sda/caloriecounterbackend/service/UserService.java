package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.entities.User;

import java.util.List;

public interface UserService {
    UserDto findById(Long userId);
    List<UserDto> findAll();
    void modify(UserDto user, String username);
    void delete(String username, String password);
    void register(UserDto user);
    void confirm(String token);
    void changePassword(String newPassword, String oldPassword, String username);

}
