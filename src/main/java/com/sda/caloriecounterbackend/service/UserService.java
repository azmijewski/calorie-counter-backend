package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.entities.User;

import java.util.List;

public interface UserService {
    User findById(Long userId);
    List<User> findAll();
    void modify(UserDto user, String username);
    void delete(String username, String password);
    void register(User user);
    void confirm(String token);
    void changePassword(String newPassword, String oldPassword, String username);

}
