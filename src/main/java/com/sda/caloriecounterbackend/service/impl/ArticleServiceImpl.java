package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.ArticleDao;
import com.sda.caloriecounterbackend.dao.PhotoDao;
import com.sda.caloriecounterbackend.dto.ArticleDto;
import com.sda.caloriecounterbackend.dto.ShortArticleDto;
import com.sda.caloriecounterbackend.entities.Article;
import com.sda.caloriecounterbackend.entities.Photo;
import com.sda.caloriecounterbackend.exception.ArticleNotFoundException;
import com.sda.caloriecounterbackend.exception.PhotoNotFoundException;
import com.sda.caloriecounterbackend.mapper.ArticleMapper;
import com.sda.caloriecounterbackend.service.ArticleService;
import com.sda.caloriecounterbackend.util.ResponseUriBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@Log4j2
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDao articleDao;
    private final PhotoDao photoDao;
    private final ArticleMapper articleMapper;
    private final ResponseUriBuilder responseUriBuilder;

    public ArticleServiceImpl(ArticleDao articleDao,
                              PhotoDao photoDao,
                              ArticleMapper articleMapper,
                              ResponseUriBuilder responseUriBuilder) {
        this.articleDao = articleDao;
        this.photoDao = photoDao;
        this.articleMapper = articleMapper;
        this.responseUriBuilder = responseUriBuilder;
    }

    @Override
    public ResponseEntity<Page<ShortArticleDto>> getArticles(int page, int size) {
        Page<ShortArticleDto> result = articleDao.getArticlesPage(page, size)
                .map(articleMapper::mapToShortArticleDto);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<ArticleDto> getArticleById(Long articleId) {
        ArticleDto articleDto;
        try {
            Article article = articleDao.getArticleById(articleId)
                    .orElseThrow(() -> new ArticleNotFoundException("Could not find article with id:" + articleId));
            articleDto = articleMapper.mapToArticleDto(article);
        } catch (ArticleNotFoundException e){
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articleDto);
    }

    @Override
    public ResponseEntity<?> saveArticle(ArticleDto articleDto) {
        Long articleId;
        try {
            Photo photo = photoDao.getById(articleDto.getPhotoId())
                    .orElseThrow(() -> new PhotoNotFoundException("Could not find photo with id:" + articleDto.getPhotoId()));
            Article articleToSave = articleMapper.mapToDb(articleDto);
            articleToSave.setPhoto(photo);
            articleId = articleDao.saveArticle(articleToSave).getId();
        } catch (PhotoNotFoundException e) {
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        URI location = responseUriBuilder.buildUri(articleId);
        return ResponseEntity.created(location).build();
    }
}
