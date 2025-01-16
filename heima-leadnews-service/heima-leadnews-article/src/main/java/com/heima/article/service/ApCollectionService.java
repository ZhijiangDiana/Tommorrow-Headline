package com.heima.article.service;

import com.heima.model.article.dtos.ArticleCollectionDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCollectionService {

    ResponseResult apCollect(ArticleCollectionDto articleCollectionDto);
}
