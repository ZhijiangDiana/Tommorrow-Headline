package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApUserProfileDto;
import com.heima.model.user.pojos.ApUser;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/7-15:35:03
 */
public interface ApUserProfileService extends IService<ApUser> {

    /**
     * 获取个人信息
     * @return
     */
    ResponseResult getUserProfile();

    /**
     * 更新个人信息
     * @param apUserProfileDto
     * @return
     */
    ResponseResult updateUserProfile(ApUserProfileDto apUserProfileDto);
}
