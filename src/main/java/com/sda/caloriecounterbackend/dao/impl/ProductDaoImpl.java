package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.ProductDao;
import com.sda.caloriecounterbackend.entities.Product;
import org.hibernate.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public class ProductDaoImpl implements ProductDao {

    private final EntityManager em;

    public ProductDaoImpl(EntityManager em) {
        this.em = em;
    }


    @Override
    public Optional<Product> findById(Long id) {
        Product product = em.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    @Override
    public Page<Product> findAll(int page, int size) {
        Query query = em.createNamedQuery("product.FindAll");
        long total = query.getResultList().size();
        query.setFirstResult(page * size);
        if (total != 0) {
            query.setMaxResults(size);
        }
        return new PageImpl<>(query.getResultList(), PageRequest.of(page, size), total);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        em.persist(product);
        return product;
    }

    @Override
    @Transactional
    public void update(Product product) {
        em.merge(product);
    }

    @Override
    @Transactional
    public void delete(Product product) {
        em.remove(product);
    }
}
