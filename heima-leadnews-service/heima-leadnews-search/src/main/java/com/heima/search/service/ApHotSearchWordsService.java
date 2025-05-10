package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/10-03:51:05
 */
public interface ApHotSearchWordsService {

    /**
     * 获取搜索热门排行榜
     * @return
     */
    ResponseResult getHotSearchWords();

    /**
     * 计算热门搜索词
     * 每小时执行，计算24小时内搜索最多的词
     */
    void calculateHotSearchWords();
}
