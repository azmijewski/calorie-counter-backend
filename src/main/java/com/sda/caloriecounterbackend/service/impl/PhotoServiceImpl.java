package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.PhotoDao;
import com.sda.caloriecounterbackend.entities.Photo;
import com.sda.caloriecounterbackend.exception.PhotoNotFoundException;
import com.sda.caloriecounterbackend.service.PhotoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


@Service
@Log4j2
public class PhotoServiceImpl implements PhotoService {

    private final PhotoDao photoDao;

    public PhotoServiceImpl(PhotoDao photoDao) {
        this.photoDao = photoDao;
    }

    @Override
    public ResponseEntity<Resource> getPhoto(Long id) {
        log.info("Get photo with id: {}", id);
        Resource resource;
        String filename;
        try {
            Photo photo = photoDao.getById(id)
                    .orElseThrow(() -> new PhotoNotFoundException("Could not find photo with id: " + id));
            filename = photo.getName();
            resource = new ByteArrayResource(photo.getBytes());
        } catch (PhotoNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,  "Content-Disposition")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Override
    public ResponseEntity<?> savePhoto(MultipartFile file) {
        log.info("Saving new photo");
        try {
            Photo photo = new Photo();
            photo.setBytes(file.getBytes());
            photo.setName(file.getOriginalFilename());
            photoDao.savePhoto(photo);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.noContent().build();
    }

}
