package com.heima.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.spider.dto.ArticleJsonInsertDto;

import java.io.IOException;
import java.text.ParseException;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/24-23:09:55
 */
public interface SpArticleService {

    /**
     * 爬虫批量插入
     * @param info
     * @throws IOException
     * @throws ParseException
     */
    void spiderBatchInsert(ArticleJsonInsertDto info) throws IOException, ParseException;
}
