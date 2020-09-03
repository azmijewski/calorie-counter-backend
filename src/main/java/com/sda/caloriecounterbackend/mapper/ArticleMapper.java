package com.sda.caloriecounterbackend.mapper;

import com.sda.caloriecounterbackend.dto.ArticleDto;
import com.sda.caloriecounterbackend.dto.ShortArticleDto;
import com.sda.caloriecounterbackend.entities.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "photoId", source = "article.photo.id")
    ArticleDto mapToArticleDto(Article article);

    @Mapping(target = "articleId", source = "article.id")
    ShortArticleDto mapToShortArticleDto(Article article);

    Article mapToDb(ArticleDto articleDto);
}
