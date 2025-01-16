package com.heima.wemedia.service;

import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsAddArticleService {

    /**
     * 将审核后的文章添加到app端文章中并设置定时发布
     * @param wmNews
     */
    void autoSaveWmNews(WmNews wmNews);
}
