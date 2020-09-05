package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dao.ArticleDao;
import com.sda.caloriecounterbackend.dao.PhotoDao;
import com.sda.caloriecounterbackend.dto.ArticleDto;
import com.sda.caloriecounterbackend.dto.ShortArticleDto;
import com.sda.caloriecounterbackend.entities.Article;
import com.sda.caloriecounterbackend.entities.Photo;
import com.sda.caloriecounterbackend.exception.PhotoNotFoundException;
import com.sda.caloriecounterbackend.mapper.ArticleMapper;
import com.sda.caloriecounterbackend.service.impl.ArticleServiceImpl;
import com.sda.caloriecounterbackend.util.ResponseUriBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks
    private ArticleServiceImpl articleService;
    @Mock
    private ArticleDao articleDao;
    @Mock
    private PhotoDao photoDao;
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private ResponseUriBuilder responseUriBuilder;

    @Test
    void shouldGetArticles() {
        //given
        when(articleDao.getArticlesPage(anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Article())));
        when(articleMapper.mapToShortArticleDto(any())).thenReturn(new ShortArticleDto());
        //when
        ResponseEntity<Page<ShortArticleDto>> result = articleService.getArticles(0, 10);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isEmpty());
    }

    @Test
    void shouldGetArticleByIdReturnArticleIfExist() {
        //given
        when(articleDao.getArticleById(anyLong())).thenReturn(Optional.of(new Article()));
        when(articleMapper.mapToArticleDto(any())).thenReturn(new ArticleDto());
        //when
        ResponseEntity<ArticleDto> result = articleService.getArticleById(1L);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }
    @Test
    void shouldGetArticleByIdNotReturnArticleIfNotExist() {
        //given
        when(articleDao.getArticleById(anyLong())).thenReturn(Optional.empty());
        //when
        ResponseEntity<ArticleDto> result = articleService.getArticleById(1L);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verifyNoInteractions(articleMapper);

    }
    @Test
    void shouldSaveArticle() throws URISyntaxException {
        //given
        when(photoDao.getById(anyLong())).thenReturn(Optional.of(new Photo()));
        when(articleMapper.mapToDb(any())).thenReturn(new Article());
        when(articleDao.saveArticle(any())).thenReturn(new Article(1L));
        when(responseUriBuilder.buildUri(anyLong())).thenReturn(new URI("http://localhost:8080"));
        //when
        ResponseEntity<?> result = articleService.saveArticle(new ArticleDto(1L));
        //then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getHeaders().containsKey("Location"));
    }
    @Test
    void shouldNotSaveArticleWhenPhotoNotFound() {
        //given
        when(photoDao.getById(anyLong())).thenThrow(PhotoNotFoundException.class);
        //when
        ResponseEntity<?> result = articleService.saveArticle(new ArticleDto(1L));
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse(result.getHeaders().containsKey("Location"));
        verifyNoInteractions(articleMapper, articleDao, responseUriBuilder);
    }

}