package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.MealProductDao;
import com.sda.caloriecounterbackend.entities.MealProduct;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class MealProductDaoImpl implements MealProductDao {

    private final EntityManager em;

    public MealProductDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void save(MealProduct mealProduct) {
        em.persist(mealProduct);
    }

    @Override
    @Transactional
    public void modify(MealProduct mealProduct) {
        em.merge(mealProduct);
    }

    @Override
    @Transactional
    public void delete(MealProduct mealProduct) {
        em.remove(mealProduct);
    }

    @Override
    public Optional<MealProduct> getByProductIdAndMealId(Long productId, Long mealId) {
        Query query = em.createNamedQuery("mealProduct.FindByProductIdAndMealId");
        try {
            MealProduct mealProduct = (MealProduct) query.getSingleResult();
            return Optional.of(mealProduct);
        } catch (NoResultException e){
            return Optional.empty();
        }
    }
}
