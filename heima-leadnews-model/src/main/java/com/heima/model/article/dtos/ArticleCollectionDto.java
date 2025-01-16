package com.heima.model.article.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleCollectionDto {

    /**
     * 文章id
     */
    private Long entryId;

    /**
     * 操作    0 收藏    1 取消收藏
     */
    private Short operation;
    public static final Short COLLECT = 0;
    public static final Short UN_COLLECT = 1;

    /**
     * 文章发布时间
     */
    private Date publishedTime;

    /**
     * 收藏类型    0 文章    1 动态
     */
    private Short type;
    public static final Short ARTICLE_COLLECT_CODE = 0;
    public static final Short MOMENT_COLLECT_CODE = 1;
}
