package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleDto implements Serializable {
    private Long id;
    private String title;
    private String content;
    private Long photoId;

    public ArticleDto(Long photoId) {
        this.photoId = photoId;
    }
}
