package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.AppJwtUtil;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-03:08:23
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {

    /**
     * app端登录功能
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(WmLoginDto dto) {
        if (!StringUtils.isNotBlank(dto.getName()) || !StringUtils.isNotBlank(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 1.1 查询用户信息
        WmUser dbUser = getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, dto.getName()));
        if (dbUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户名或密码错误");

        // 1.1.1检查是否被封禁
        if (!WmUser.AVAILABLE.equals(dbUser.getStatus()))
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_IS_BANNED, "用户已被封禁");

        // 1.2 比对密码
        String salt = dbUser.getSalt();
        String password = dto.getPassword();
        String pswdWithSalt = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if (!pswdWithSalt.equals(dbUser.getPassword()))
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);

        // 1.3 返回数据 jwt
        String token = AppJwtUtil.getToken(dbUser.getId().longValue());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        dbUser.setSalt("");
        dbUser.setPassword("");
        map.put("user", dbUser);
        return ResponseResult.okResult(map);
    }
}
