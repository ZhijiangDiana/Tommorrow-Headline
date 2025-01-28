package com.heima.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.spider.dto.ArticleJsonInsertDto;
import com.heima.service.SpArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/spider")
public class SpArticleController {

    @Autowired
    private SpArticleService spArticleService;

    @PostMapping("/spider_insert")
    public ResponseResult spiderInsert(ArticleJsonInsertDto dto) throws IOException, ParseException {
        spArticleService.spiderBatchInsert(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
