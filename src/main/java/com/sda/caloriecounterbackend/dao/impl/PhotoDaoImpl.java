package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.PhotoDao;
import com.sda.caloriecounterbackend.entities.Photo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class PhotoDaoImpl implements PhotoDao {

    private final EntityManager em;

    public PhotoDaoImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public Optional<Photo> getById(Long id) {
        Photo photo = em.find(Photo.class, id);
        return Optional.ofNullable(photo);
    }

    @Override
    @Transactional
    public void savePhoto(Photo photo) {
        em.persist(photo);
    }
}
