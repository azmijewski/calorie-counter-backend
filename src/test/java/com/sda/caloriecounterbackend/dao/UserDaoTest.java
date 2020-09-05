package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.dao.impl.UserDaoImpl;
import com.sda.caloriecounterbackend.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    @InjectMocks
    private UserDaoImpl userDao;
    @Mock
    private EntityManager em;
    @Mock
    private Query query;

    @Test
    void shouldFindByIdReturnUserIfExist() {
        //given
        when(em.find(any(), anyLong())).thenReturn(new User());
        //when
        Optional<User> result = userDao.findById(1L);
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldFindByIdNotReturnUserIfNotExist() {
        //given
        when(em.find(any(), anyLong())).thenReturn(null);
        //when
        Optional<User> result = userDao.findById(1L);
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldSave() {
        //given
        doNothing().when(em).persist(any());
        //when
        userDao.save(new User());
        //then
        verify(em).persist(any());
    }
    @Test
    void shouldModify() {
        //given
        when(em.merge(any())).thenReturn(new User());
        //when
        userDao.modify(new User());
        //then
        verify(em).merge(any());
    }
    @Test
    void shouldDelete() {
        //given
        doNothing().when(em).remove(any());
        //when
        userDao.delete(new User());
        //then
        verify(em).remove(any());
    }
    @Test
    void shouldGetAll(){
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(new User()));
        //when
        List<User> result = userDao.getAll();
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldFindByUsernameReturnUserIfExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new User());
        //when
        Optional<User> result = userDao.findByUsername("test");
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldFindByUsernameNotReturnUserIfNotExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);
        //when
        Optional<User> result = userDao.findByUsername("test");
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldFindByTokenReturnUserIfExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new User());
        //when
        Optional<User> result = userDao.findByToken("test");
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldFindByTokenNotReturnUserIfNotExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);
        //when
        Optional<User> result = userDao.findByToken("test");
        //then
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldExistByLoginOrMailReturnTrueIfExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(new User()));
        //when
        Boolean result = userDao.existByLoginOrMail("test", "test@test.pl");
        assertTrue(result);
    }
    @Test
    void shouldExistByLoginOrMailReturnFalseIfNotExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());
        //when
        Boolean result = userDao.existByLoginOrMail("test", "test@test.pl");
        assertFalse(result);
    }

}