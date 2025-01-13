package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserLoginMapper;
import com.heima.admin.service.AdUserLoginService;
import com.heima.admin.service.AdUserService;
import com.heima.common.baidu.AddressService;
import com.heima.model.admin.dtos.AddressDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.admin.pojos.AdUserLogin;
import com.heima.utils.net.IPUtils;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Service
public class AdUserLoginServiceImpl extends ServiceImpl<AdUserLoginMapper, AdUserLogin> implements AdUserLoginService {

    @Lazy
    @Autowired
    private AdUserService adUserService;

    @Autowired
    private AddressService addressService;

    @Override
    @Transactional
    public void recordLogin(HttpServletRequest request, Integer apId) throws IOException {
        // 记录登录日志
        // 添加用户id
        AdUserLogin adUserLogin = new AdUserLogin();
        adUserLogin.setUserId(apId);

        // 添加用户ip及其归属地等信息
        String ipAddress = request.getRemoteAddr();
        if (ipAddress.equals("0:0:0:0:0:0:0:1"))
            ipAddress = "127.0.0.1";
        adUserLogin.setIp(ipAddress);

        AddressDto address;
        if (IPUtils.isPrivateIP(ipAddress)) {
            // 若为本地ip，则返回服务器地址
            address = adUserService.getServerAddress();
        } else {
            // 若为外部ip，则查找地址
            address = addressService.getAddressByIP(ipAddress);
        }
        adUserLogin.setAddress(address.getAddress());
        adUserLogin.setLatitude(address.getX());
        adUserLogin.setLongitude(address.getY());

        // 添加日期
        adUserLogin.setCreatedTime(new Date());

        // 保存
        save(adUserLogin);
    }
}
