package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-19:29:37
 */
@Component
public class IArticleClientFallback implements IArticleClient {

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult getArticleByTitle(ApArticle article) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
