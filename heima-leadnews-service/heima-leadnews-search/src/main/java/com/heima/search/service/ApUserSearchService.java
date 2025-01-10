package com.heima.search.service;

public interface ApUserSearchService {

    /**
     * 添加搜索记录
     * @param keyword
     * @param userId
     */
    void addSearchHistory(String keyword, Integer userId);
}
