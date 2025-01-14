package com.heima.wemedia.service;

import com.heima.model.wemedia.pojos.WmNews;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-14:01:27
 */
public interface WmNewsAutoScanService {

    /**
     * 自媒体文章申鹤
     * @param id
     */
    void autoScanWmNews(Integer id);

    /**
     * 将审核后的文章添加到app端文章中并设置定时发布
     * @param wmNews
     */
    void autoSaveWmNews(WmNews wmNews);
}
