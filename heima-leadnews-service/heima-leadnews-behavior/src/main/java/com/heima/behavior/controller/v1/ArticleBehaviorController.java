package com.heima.behavior.controller.v1;

import com.heima.behavior.service.ArticleBehaviorService;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ArticleBehaviorController {

    @Autowired
    private ArticleBehaviorService articleBehaviorService;

    @PostMapping("/likes_behavior")
    public ResponseResult likesBehavior(@RequestBody LikesBehaviorDto dto) {
        return articleBehaviorService.like(dto);
    }

    @PostMapping("/read_behavior")
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto) {
        return articleBehaviorService.read(dto);
    }

    @PostMapping("/un_likes_behavior")
    public ResponseResult dislikeBehavior(@RequestBody DislikeBehaviorDto dto) {
        return articleBehaviorService.dislike(dto);
    }
}
