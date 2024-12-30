package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/30-04:14:13
 */
public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传到minio中
     * @param apArticle
     * @param content
     */
    void buildArticleToMinio(ApArticle apArticle, String content);
}
