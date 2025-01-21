package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.CodeLoginDto;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.dtos.RegisterDto;
import com.heima.model.user.pojos.ApUser;

public interface ApUserService extends IService<ApUser> {

    /**
     * app端登录功能
     * @param dto
     * @return
     */
    ResponseResult login(LoginDto dto);

    /**
     * app端手机登录功能
     * @param dto
     * @return
     */
    ResponseResult loginByCode(CodeLoginDto dto);

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    ResponseResult sendVerifyCode(String phone);

    /**
     * 用户注册
     * @param dto
     * @return
     */
    ResponseResult register(RegisterDto dto);
}
