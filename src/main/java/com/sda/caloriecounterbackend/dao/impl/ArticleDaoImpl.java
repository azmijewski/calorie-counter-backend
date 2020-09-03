package com.sda.caloriecounterbackend.dao.impl;

import com.sda.caloriecounterbackend.dao.ArticleDao;
import com.sda.caloriecounterbackend.entities.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Optional;
@Repository
@Transactional(readOnly = true)
public class ArticleDaoImpl implements ArticleDao {

    private final EntityManager em;

    public ArticleDaoImpl(EntityManager em) {
        this.em = em;
    }


    @Override
    public Page<Article> getArticlesPage(int page, int size) {
        Query query = em.createNamedQuery("article.FindAllOrderByDate");
        long total = query.getResultList().size();
        query.setFirstResult(page * size);
        if (total != 0) {
            query.setMaxResults(size);
        }
        return new PageImpl<>(query.getResultList(), PageRequest.of(page, size), total);
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        Article article = em.find(Article.class, id);
        return Optional.ofNullable(article);
    }

    @Override
    @Transactional
    public Article saveArticle(Article article) {
        em.persist(article);
        return article;
    }
}
