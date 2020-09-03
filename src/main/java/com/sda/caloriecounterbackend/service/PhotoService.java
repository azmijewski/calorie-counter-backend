package com.sda.caloriecounterbackend.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    ResponseEntity<Resource> getPhoto(Long id);
    ResponseEntity<?> savePhoto(MultipartFile file);
}
