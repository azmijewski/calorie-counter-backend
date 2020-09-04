package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.ProductSearchDao;
import com.sda.caloriecounterbackend.entities.Product;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Objects;
@Repository
@Transactional(readOnly = true)
public class ProductSearchDaoImpl implements ProductSearchDao {


    private static final String NAME = "name";
    private static final String BRAND = "brand";
    private static final String SEPARATOR = "\\s+";


    private final EntityManager em;


    public ProductSearchDaoImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findByCriteria(String search, int page, int size) {
        String[] searchWords = getSearchWords(search);
        QueryBuilder queryBuilder = Search.getFullTextEntityManager(this.em)
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Product.class)
                .get();
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        for (String searchWord : searchWords) {
            Query query = queryBuilder.keyword()
                    .onFields(NAME, BRAND)
                    .matching(searchWord)
                    .createQuery();
            booleanQueryBuilder.add(query, BooleanClause.Occur.MUST);
        }
        FullTextQuery jpaQuery = Search.getFullTextEntityManager(this.em)
                .createFullTextQuery(booleanQueryBuilder.build(), Product.class);
        jpaQuery.setFirstResult(page * size);
        int totalSize = jpaQuery.getResultSize();
        if (size != 0) {
            jpaQuery.setMaxResults(size);
        }
        return createPageResponse(jpaQuery.getResultList(), page, size, totalSize);
    }


    private String[] getSearchWords(String search) {
        String[] wordsToSearch = StringUtils.split(search, SEPARATOR);
        if(Objects.isNull(wordsToSearch)) {
            wordsToSearch = new String[] {search};
        }
        validateSearchWords(wordsToSearch);
        return wordsToSearch;
    }
    private void validateSearchWords(String[] searchWords) {
        for (String searchWord : searchWords) {
            if (searchWord.matches(SEPARATOR)) {
                throw new IllegalArgumentException("Search word: " + searchWord + " is invalid");
            }
        }
    }
    private   Page<Product> createPageResponse(List<Product> data, int page, int size, int totalSize ) {
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(data, pageable, totalSize);
    }
}
