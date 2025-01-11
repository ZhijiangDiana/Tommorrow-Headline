package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;

public interface ApUserSearchService {

    /**
     * 添加搜索记录
     * @param keyword
     * @param userId
     */
    void addSearchHistory(String keyword, Integer userId);

    ResponseResult getSearchHistory();

    ResponseResult deleteSearchHistory(String id);
}
