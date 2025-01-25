package com.heima.article.service;


public interface HotArticleService {

    /**
     * 同步文章数据
     */
    void syncArticleInfo();

    /**
     * 计算热点文章
     */
    void computeHotArticle();

}
