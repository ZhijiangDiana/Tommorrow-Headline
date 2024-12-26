package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-03:06:30
 */
public interface WmUserService extends IService<WmUser> {

    public ResponseResult login(WmLoginDto dto);
}
