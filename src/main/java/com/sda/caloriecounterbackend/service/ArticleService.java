package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dto.ArticleDto;
import com.sda.caloriecounterbackend.dto.ShortArticleDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ArticleService {
    ResponseEntity<Page<ShortArticleDto>> getArticles(int page, int size);
    ResponseEntity<ArticleDto> getArticleById(Long articleId);
    ResponseEntity<?> saveArticle(ArticleDto articleDto);
}
