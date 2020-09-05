package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.dao.impl.PhotoDaoImpl;
import com.sda.caloriecounterbackend.entities.Photo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoDaoTest {
    @InjectMocks
    private PhotoDaoImpl photoDao;
    @Mock
    private EntityManager em;
    @Mock
    private Query query;

    @Test
    void shouldReturnPhotoIfExist() {
        //given
        when(em.find(any(), anyLong())).thenReturn(new Photo());
        //when
        Optional<Photo> result = photoDao.getById(1L);
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldNotReturnPhotoIfNotExist() {
        //given
        when(em.find(any(), anyLong())).thenReturn(null);
        //when
        Optional<Photo> result = photoDao.getById(1L);
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldSave() {
        //given
        doNothing().when(em).persist(any());
        //when
        photoDao.savePhoto(new Photo());
        //then
        verify(em).persist(any());
    }


}