package com.heima.article.controller.v1;

import com.heima.article.service.ApCollectionService;
import com.heima.model.article.dtos.ArticleCollectionDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ArticleCollectionController {

    @Autowired
    private ApCollectionService apCollectionService;

    @PostMapping("/collection_behavior")
    public ResponseResult collect(@RequestBody ArticleCollectionDto dto) {
        return apCollectionService.apCollect(dto);
    }
}
