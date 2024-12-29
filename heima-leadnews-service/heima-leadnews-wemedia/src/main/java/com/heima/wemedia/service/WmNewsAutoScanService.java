package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-14:01:27
 */
public interface WmNewsAutoScanService {

    /**
     * 自媒体文章申鹤
     *
     * @param id
     * @return
     */
    ResponseResult autoScanWnNews(Integer id);
}
