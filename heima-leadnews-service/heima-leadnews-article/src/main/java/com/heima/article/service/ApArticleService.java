package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/24-23:09:55
 */
public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     * @param dto
     * @param type
     * @return 1 加载更多     2 加载最新
     */
    public ResponseResult load(ArticleHomeDto dto, Short type);
}
