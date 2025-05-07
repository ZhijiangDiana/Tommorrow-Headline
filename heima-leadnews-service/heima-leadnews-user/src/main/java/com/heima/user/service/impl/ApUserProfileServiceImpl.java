package com.heima.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApUserProfileDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserProfileService;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/7-15:37:14
 */
@Service
public class ApUserProfileServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserProfileService {

    @Override
    public ResponseResult getUserProfile() {
        // 验证登录
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 获取信息
        ApUser apUser = getById(userId);
        ApUserProfileDto apUserProfileDto = new ApUserProfileDto();

        // 组装实体
        BeanUtils.copyProperties(apUser, apUserProfileDto);
        apUserProfileDto.setPassword("******");

        // 返回
        return ResponseResult.okResult(apUserProfileDto);
    }

    @Override
    public ResponseResult updateUserProfile(ApUserProfileDto apUserProfileDto) {
        // 验证登录
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 组装实体
        ApUser apUser = new ApUser();
        BeanUtils.copyProperties(apUserProfileDto, apUser);
        apUser.setId(userId);
        apUser.setPassword(null);
        apUser.setPhone(null);

        // 更新用户资料
        updateById(apUser);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
