package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class UserDaoImpl implements UserDao {

    private final EntityManager em;

    public UserDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Query query = em.createNamedQuery("user.FindByUserName");
        query.setParameter("username", username);
        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> getAll() {
        Query query = em.createQuery("SELECT u from User u");
        return query.getResultList();
    }

    @Override
    public User save(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public void modify(User user) {
        em.merge(user);
    }

    @Override
    public void delete(User user) {
        em.remove(user);
    }

    @Override
    public Optional<User> findByToken(String token) {
        Query query = em.createNamedQuery("user.FindByToken");
        query.setParameter("token", token);
        try {
            User user = (User) query.getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e){
            return Optional.empty();
        }
    }
}
