package com.sda.caloriecounterbackend.dao;

import com.sda.caloriecounterbackend.entities.Article;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ArticleDao {
    Page<Article> getArticlesPage(int page, int size);
    Optional<Article> getArticleById(Long id);
    Article saveArticle(Article article);
}
