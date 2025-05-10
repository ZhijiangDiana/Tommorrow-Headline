package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.service.ApHotSearchWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/10-17:09:54
 */
@RestController
@RequestMapping("/api/v1/hot_keywords")
public class ApHotSearchWordsController {

    @Autowired
    private ApHotSearchWordsService apHotSearchWordsService;

    @PostMapping("/load")
    public ResponseResult loadHotSearchWords() {
        return apHotSearchWordsService.getHotSearchWords();
    }
}
