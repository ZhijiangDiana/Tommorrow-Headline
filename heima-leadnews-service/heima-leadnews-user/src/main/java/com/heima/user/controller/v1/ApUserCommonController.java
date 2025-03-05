package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ApUserCommonController {

    @Autowired
    private ApUserCommonService apUserCommonService;

    @GetMapping("/info")
    public ResponseResult getUserInfo() {
        return apUserCommonService.getUserInfo();
    }

    /**
     * 用apId查找粉丝列表
     * @return
     */
    @GetMapping("/fans")
    public ResponseResult getFans() {
        return apUserCommonService.getFansList();
    }

    /**
     * 用apId查找关注列表
     * @return
     */
    @GetMapping("/following")
    public ResponseResult getFollowing() {
        return apUserCommonService.getFollowingList();
    }
}
