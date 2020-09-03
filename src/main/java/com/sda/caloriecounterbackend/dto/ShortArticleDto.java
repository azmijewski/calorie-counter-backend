package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShortArticleDto implements Serializable {
    private Long articleId;
    private String title;
}
