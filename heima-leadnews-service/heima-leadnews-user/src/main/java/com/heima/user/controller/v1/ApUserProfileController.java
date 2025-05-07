package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApUserProfileDto;
import com.heima.user.service.ApUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/7-15:33:09
 */
@RestController
@RequestMapping("/api/v1/profile")
public class ApUserProfileController {

    @Autowired
    private ApUserProfileService apUserProfileService;

    /**
     * 获取个人信息
     * @return
     */
    @PostMapping("/getProfile")
    public ResponseResult getProfile() {
        return apUserProfileService.getUserProfile();
    }

    /**
     * 修改个人信息
     * @return
     */
    @PostMapping("/updateProfile")
    public ResponseResult updateProfile(@RequestBody ApUserProfileDto apUserProfileDto) {
        return apUserProfileService.updateUserProfile(apUserProfileDto);
    }
}
