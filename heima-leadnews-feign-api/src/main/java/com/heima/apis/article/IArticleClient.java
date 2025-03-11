package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.article.dtos.ArticleCommentDto;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.comment.dtos.CommentConfigDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.StatisticsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-03:15:06
 */
@FeignClient(value = "leadnews-article", fallback = IArticleClientFallback.class)  // 名称不能带/_
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(ArticleDto dto);

    @PostMapping("/api/v1/article/getByTitle")
    ResponseResult getArticleByTitle(ApArticle apArticle);

    @GetMapping("/api/v1/article/queryLikesAndConllections")
    ResponseResult queryLikesAndConllections(@RequestParam("wmUserId") Integer wmUserId,
                                             @RequestParam("beginDate") Date beginDate,
                                             @RequestParam("endDate") Date endDate);

    @PostMapping("/api/v1/article/newPage")
    PageResponseResult newPage(@RequestBody StatisticsDto dto);

    @GetMapping("/api/v1/article/findArticleConfigByArticleId/{articleId}")
    ResponseResult findArticleConfigByArticleId(@PathVariable("articleId") Long articleId);

    @PostMapping("/api/v1/article/findNewsComments")
    PageResponseResult findNewsComments(@RequestBody ArticleCommentDto dto);

    @PostMapping("/api/v1/article/updateCommentStatus")
    ResponseResult updateCommentStatus(@RequestBody CommentConfigDto dto);
}
