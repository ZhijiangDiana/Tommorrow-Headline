package com.heima.article.service;

import com.heima.model.mess.ArticleVisitStreamMess;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/12-02:39:27
 */
public interface HotArticleStreamService {

    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     * @param mess
     */
    void updateScore(ArticleVisitStreamMess mess);
}
