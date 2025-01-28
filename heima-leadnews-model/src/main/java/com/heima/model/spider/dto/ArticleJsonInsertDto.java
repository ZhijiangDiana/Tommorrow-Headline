package com.heima.model.spider.dto;

import lombok.Data;

@Data
public class ArticleJsonInsertDto {
    private String basePath;
    private String searchWord;
    private Integer channelId;
    private String channelName;
    private Long authorId = 1102L;
    private String authorName = "admin";
}