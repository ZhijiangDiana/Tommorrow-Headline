package com.heima.apis.user;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("leadnews-user")
public interface IUserClient {

    @GetMapping("/api/v1/user/{id}")
    ApUser findUserById(@PathVariable("id") Integer id);

    @PostMapping("/api/v1/user/batch")
    List<ApUser> findUserByBatch(@RequestParam("ids") List<Integer> ids);

    @PostMapping("/api/v1/follow/follow")
    ResponseResult followOrUnfollow(
            @RequestHeader("userId") Integer userId,
            @RequestBody UserRelationDto userRelationDto);
}
