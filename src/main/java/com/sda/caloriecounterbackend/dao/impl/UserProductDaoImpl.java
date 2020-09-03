package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.UserProductDao;
import com.sda.caloriecounterbackend.entities.UserProduct;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class UserProductDaoImpl implements UserProductDao {

    private final EntityManager em;

    public UserProductDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<UserProduct> findAllByDateAndUsername(LocalDate localDate, String username) {
        Query query = em.createNamedQuery("userProduct.FindByDateAndUserName");
        query.setParameter("date", localDate);
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    @Transactional
    public UserProduct save(UserProduct userProduct) {
       em.persist(userProduct);
       return userProduct;
    }

    @Override
    public Optional<UserProduct> findByUsernameAndDateAndProductId(String username, LocalDate date, Long productId) {
        Query query = em.createNamedQuery("userProduct.FindByProductAndDateAndUsername");
        query.setParameter("id", productId);
        query.setParameter("username", username);
        query.setParameter("date", date);
        UserProduct userProduct;
        try {
            userProduct = (UserProduct) query.getSingleResult();
        } catch (NoResultException e){
            return Optional.empty();
        }
        return Optional.of(userProduct);
    }

    @Override
    @Transactional
    public void modify(UserProduct userProduct) {
        em.merge(userProduct);
    }
}
