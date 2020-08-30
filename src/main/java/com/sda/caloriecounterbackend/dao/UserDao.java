package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> getAll();
    User save(User user);
    void modify(User user);
    void delete(User user);
    Optional<User> findByToken(String token);
}
