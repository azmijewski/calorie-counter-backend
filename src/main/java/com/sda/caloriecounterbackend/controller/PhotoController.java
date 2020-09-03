package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.service.PhotoService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/photos")
@CrossOrigin(origins = "*")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<Resource> downloadPhoto(@PathVariable Long photoId) {
        return photoService.getPhoto(photoId);
    }

    @PostMapping
    public ResponseEntity<?> uploadPhoto(@RequestParam("photo") MultipartFile multipartFile) {
        return photoService.savePhoto(multipartFile);
    }
}
