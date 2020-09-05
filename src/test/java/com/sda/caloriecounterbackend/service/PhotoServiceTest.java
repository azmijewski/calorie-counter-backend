package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dao.PhotoDao;
import com.sda.caloriecounterbackend.entities.Photo;
import com.sda.caloriecounterbackend.service.impl.PhotoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {
    @InjectMocks
    private PhotoServiceImpl photoService;
    @Mock
    private PhotoDao photoDao;

    @Test
    void shouldGetPhoto() {
        //given
        when(photoDao.getById(anyLong())).thenReturn(Optional.of(new Photo(new byte[10])));
        //when
        ResponseEntity<Resource> result = photoService.getPhoto(1L);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
       assertNotNull(result.getBody());
    }
    @Test
    void shouldNotGetPhotoIfNotExist() {
        //given
        when(photoDao.getById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<Resource> result = photoService.getPhoto(1L);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }
    @Test
    void shouldSavePhoto() {
        //given
        MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg".getBytes());
        doNothing().when(photoDao).savePhoto(any());
        //when
        ResponseEntity<?> result = photoService.savePhoto(file);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

}