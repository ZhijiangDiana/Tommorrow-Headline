package com.heima.model.article.dtos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-03:16:50
 */
@Data
public class ArticleDto extends ApArticle {

    /**
     * 文章内容
     */
    private String content;
}
