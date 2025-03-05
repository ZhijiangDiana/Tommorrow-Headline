package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleHistoryDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

public interface ApHistoryStarArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     * @param dto
     * @return 1 加载更多     2 加载最新
     */
    ResponseResult load(ArticleHistoryDto dto);
}
