package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.dao.impl.UserProductDaoImpl;
import com.sda.caloriecounterbackend.entities.UserProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProductDaoTest {
    @InjectMocks
    private UserProductDaoImpl userProductDao;
    @Mock
    private EntityManager em;
    @Mock
    private Query query;

    @Test
    void shouldFindAllByDateAndUsername() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(new UserProduct()));
        //when
        List<UserProduct> result = userProductDao.findAllByDateAndUsername(LocalDate.now(), "test");
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldSave() {
        //given
        doNothing().when(em).persist(any());
        //when
        userProductDao.save(new UserProduct());
        //then
        verify(em).persist(any());
    }
    @Test
    void shouldModify() {
        //given
        when(em.merge(any())).thenReturn(new UserProduct());
        //when
        userProductDao.modify(new UserProduct());
        //then
        verify(em).merge(any());
    }
    @Test
    void shouldDelete() {
        //given
        doNothing().when(em).remove(any());
        //when
        userProductDao.remove(new UserProduct());
        //then
        verify(em).remove(any());
    }
    @Test
    void shouldFindByUsernameAndDateAndProductIdReturnUserProductIfExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new UserProduct());
        //when
        Optional<UserProduct> result =
                userProductDao.findByUsernameAndDateAndProductId("test", LocalDate.now(), 1L);
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldFindByUsernameAndDateAndProductIdNotReturnUserProductIfNotExist() {
        //given
        when(em.createNamedQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(NoResultException.class);
        //when
        Optional<UserProduct> result =
                userProductDao.findByUsernameAndDateAndProductId("test", LocalDate.now(), 1L);
        //then
        assertTrue(result.isEmpty());
    }


}