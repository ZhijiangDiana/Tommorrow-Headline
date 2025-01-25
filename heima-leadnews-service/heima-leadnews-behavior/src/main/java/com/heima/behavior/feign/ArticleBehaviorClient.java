package com.heima.behavior.feign;

import com.heima.apis.behavior.IBehaviorClient;
import com.heima.behavior.service.ArticleBehaviorService;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("leadnews-behavior")
public class ArticleBehaviorClient implements IBehaviorClient {

    @Autowired
    private ArticleBehaviorService articleBehaviorService;

    @Override
    @PostMapping("/api/v1/add_like")
    public ResponseResult likesBehavior(@RequestBody LikesBehaviorDto dto) {
        return articleBehaviorService.like(dto);
    }

    @Override
    @PostMapping("/api/v1/add_read")
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto) {
        return articleBehaviorService.read(dto);
    }

    @Override
    @PostMapping("/api/v1/add_un_likes")
    public ResponseResult dislikeBehavior(@RequestBody DislikeBehaviorDto dto) {
        return articleBehaviorService.dislike(dto);
    }
}
