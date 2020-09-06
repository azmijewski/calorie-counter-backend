package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.MealDao;
import com.sda.caloriecounterbackend.entities.Meal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class MealDaoImpl implements MealDao {

    private final EntityManager em;

    public MealDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<Meal> getUserAndDefault(String username, int page, int size) {
        Query query = em.createNamedQuery("meal.FindByUsernameAndDefaults");
        query.setParameter("username", username);
        long total = query.getResultList().size();
        query.setFirstResult(page * size);
        if(total != 0) {
            query.setMaxResults(size);
        }
        return new PageImpl<>(query.getResultList(), PageRequest.of(page, size), total);
    }

    @Override
    public Optional<Meal> getById(Long id) {
      Query query = em.createNamedQuery("meal.FindByIdJoinProducts");
      query.setParameter("id", id);
      try {
          Meal meal = (Meal) query.getSingleResult();
          return Optional.of(meal);
      } catch (NoResultException e) {
          return Optional.empty();
      }
    }

    @Override
    @Transactional
    public Meal save(Meal meal) {
        em.persist(meal);
        return meal;
    }

    @Override
    @Transactional
    public void modify(Meal meal) {
        em.merge(meal);
    }

    @Override
    @Transactional
    public void delete(Meal meal) {
        em.remove(meal);
    }
}
