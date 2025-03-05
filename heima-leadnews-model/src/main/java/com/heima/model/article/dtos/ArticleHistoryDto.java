package com.heima.model.article.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleHistoryDto extends ArticleHomeDto {

    // 检索的类型，有__history__和__star__
    String type;

    public static String LOAD_HISTORY = "__history__";
    public static String LOAD_STAR = "__star__";
}