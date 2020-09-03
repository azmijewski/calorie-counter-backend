package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.ArticleDto;
import com.sda.caloriecounterbackend.dto.ShortArticleDto;
import com.sda.caloriecounterbackend.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<Page<ShortArticleDto>> getArticles(@RequestParam(name = "page") int page,
                                                            @RequestParam(name = "size") int size) {
        return articleService.getArticles(page, size);
    }
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable Long articleId) {
        return articleService.getArticleById(articleId);
    }
    @PostMapping
    public ResponseEntity<?> saveArticle(@RequestBody @Valid ArticleDto articleDto) {
        return articleService.saveArticle(articleDto);
    }
}
