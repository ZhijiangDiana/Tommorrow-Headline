package com.heima.article.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleCommentDto;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.comment.dtos.CommentConfigDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-03:18:30
 */
@RestController("leadnews-article")
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService apArticleService;

    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }

    @Override
    @PostMapping("/api/v1/article/getByTitle")
    public ResponseResult getArticleByTitle(@RequestBody ApArticle article) {
        List<ApArticle> list = apArticleService.list(new LambdaQueryWrapper<ApArticle>()
                .select(ApArticle::getTitle)
                .eq(ApArticle::getTitle, article.getTitle()));
        return ResponseResult.okResult(!list.isEmpty() ? list.get(0) : null);
    }

    @GetMapping("/api/v1/article/queryLikesAndConllections")
    @Override
    public ResponseResult queryLikesAndConllections(@RequestParam("wmUserId") Integer wmUserId,
                                                    @RequestParam("beginDate") Date beginDate,
                                                    @RequestParam("endDate") Date endDate) {
        return apArticleService.queryLikesAndConllections(wmUserId,beginDate,endDate);
    }

    @PostMapping("/api/v1/article/newPage")
    @Override
    public PageResponseResult newPage(@RequestBody StatisticsDto dto) {
        return apArticleService.newPage(dto);
    }

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @GetMapping("/api/v1/article/findArticleConfigByArticleId/{articleId}")
    @Override
    public ResponseResult findArticleConfigByArticleId(@PathVariable("articleId") Long articleId) {
        ApArticleConfig apArticleConfig = apArticleConfigService.getOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, articleId));
        return ResponseResult.okResult(apArticleConfig);
    }

    @PostMapping("/api/v1/article/findNewsComments")
    @Override
    public PageResponseResult findNewsComments(@RequestBody ArticleCommentDto dto) {
        return apArticleService.findNewsComments(dto);
    }

    @PostMapping("/api/v1/article/updateCommentStatus")
    @Override
    public ResponseResult updateCommentStatus(@RequestBody CommentConfigDto dto) {
        return apArticleConfigService.updateCommentStatus(dto);
    }
}
