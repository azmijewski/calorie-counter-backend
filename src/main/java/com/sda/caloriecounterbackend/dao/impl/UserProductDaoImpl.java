package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.UserProductDao;
import com.sda.caloriecounterbackend.entities.UserProduct;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class UserProductDaoImpl implements UserProductDao {

    private final EntityManager em;

    public UserProductDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<UserProduct> findAllByDate(LocalDate localDate, String username) {
        Query query = em.createNamedQuery("userProduct.FindByDateAndUserName");
        query.setParameter("date", localDate);
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public UserProduct save(UserProduct userProduct) {
       em.persist(userProduct);
       return userProduct;
    }
}
