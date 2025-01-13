package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.pojos.AdUserLogin;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface AdUserLoginService extends IService<AdUserLogin> {

    /**
     * 记录管理员登录日志
     * @param request
     */
    void recordLogin(HttpServletRequest request, Integer apId) throws IOException;
}
