package com.heima.apis.article;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-03:15:06
 */
@FeignClient("leadnews-article")  // 名称不能带/_
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(ArticleDto dto);
}
