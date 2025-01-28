package com.heima.article.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseResult getArticleByTitle(String title) {
        return ResponseResult.okResult(apArticleService.getOne(new LambdaQueryWrapper<ApArticle>()
                .select(ApArticle::getTitle)
                .eq(ApArticle::getTitle, title)));
    }
}
