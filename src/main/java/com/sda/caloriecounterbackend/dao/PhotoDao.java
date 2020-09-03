package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.Photo;

import java.util.Optional;

public interface PhotoDao {
    Optional<Photo> getById(Long id);
    void savePhoto(Photo photo);
}
