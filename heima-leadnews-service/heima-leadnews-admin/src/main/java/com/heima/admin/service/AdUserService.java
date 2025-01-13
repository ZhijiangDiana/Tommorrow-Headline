package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AddressDto;
import com.heima.model.admin.dtos.LoginDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface AdUserService extends IService<AdUser> {

    /**
     * 管理员登录
     * @param loginDto
     * @return
     */
    ResponseResult login(HttpServletRequest request, LoginDto loginDto);

    AddressDto getServerAddress() throws IOException;
}
