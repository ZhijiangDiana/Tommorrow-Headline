package com.heima.article.controller.v1;

import com.heima.article.service.ApHistoryStarArticleService;
import com.heima.model.article.dtos.ArticleHistoryDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/history")
public class ArticleApUserController {

    @Autowired
    public ApHistoryStarArticleService apHistoryStarArticleService;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHistoryDto dto) {
        return apHistoryStarArticleService.load(dto);
    }
}
