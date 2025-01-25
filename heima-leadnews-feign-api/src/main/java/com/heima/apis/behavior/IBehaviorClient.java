package com.heima.apis.behavior;

import com.heima.apis.behavior.fallback.IBehaviorClientFallback;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-behavior", fallback = IBehaviorClientFallback.class)
public interface IBehaviorClient {

    @PostMapping("/api/v1/add_like")
    ResponseResult likesBehavior(@RequestBody LikesBehaviorDto dto);

    @PostMapping("/api/v1/add_read")
    ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto);

    @PostMapping("/api/v1/add_un_likes")
    ResponseResult dislikeBehavior(@RequestBody DislikeBehaviorDto dto);
}