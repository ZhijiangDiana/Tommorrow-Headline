package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserLoginService;
import com.heima.admin.service.AdUserService;
import com.heima.common.baidu.AddressService;
import com.heima.model.admin.dtos.AddressDto;
import com.heima.model.admin.dtos.LoginDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {

    private static AddressDto serverAddress = null;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AdUserLoginService adUserLoginService;

    @Override
    @Transactional
    public ResponseResult login(HttpServletRequest request, LoginDto dto) {
        if (!StringUtils.isNotBlank(dto.getName()) || !StringUtils.isNotBlank(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 1.1 查询用户信息
        AdUser dbUser = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
        if (dbUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户名或密码错误");

        // 1.2 比对密码
        String salt = dbUser.getSalt();
        String password = dto.getPassword();
        String pswdWithSalt = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if (!pswdWithSalt.equals(dbUser.getPassword()))
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);

        // 新增 添加管理员登录日志
        try {
            adUserLoginService.recordLogin(request, dbUser.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        // 1.3 返回数据 jwt
        String token = AppJwtUtil.getToken(dbUser.getId().longValue());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        dbUser.setSalt("");
        dbUser.setPassword("");
        map.put("user", dbUser);
        return ResponseResult.okResult(map);
    }

    @Override
    public AddressDto getServerAddress() throws IOException {
        if (serverAddress == null)
            serverAddress = addressService.getAddressByIP("");
        return serverAddress;
    }
}
