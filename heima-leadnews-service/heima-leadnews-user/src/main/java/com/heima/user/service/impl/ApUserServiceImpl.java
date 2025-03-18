package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.aliyun.SMSService;
import com.heima.common.constants.ApUserConstants;
import com.heima.common.jwt.AppJwtUtil;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.CodeLoginDto;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.dtos.RegisterDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
@ConfigurationProperties(prefix = "minio")
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private AppJwtUtil appJwtUtil;

    private static final long CODE_VALID_MINUTES = 30; // 1; //30;

    private static final Random random = new Random();

    @Value("${minio.readPath}")
    private String readPath;

    private static final String defaultUserImage = "/leadnews/default_avatar.jpeg";

    /**
     * app端登录功能
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        // 1. 正常登录 用户名和密码
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            // 1.1 查询用户信息
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (dbUser == null)
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户信息不存在");

            // 1.1.1检查是否被封禁
            if (dbUser.getStatus())
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_IS_BANNED);

            // 1.2 比对密码
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pswdWithSalt = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pswdWithSalt.equals(dbUser.getPassword()))
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);

            // 1.3 返回数据 jwt
            String token = appJwtUtil.getToken(dbUser.getId().longValue());
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("user", dbUser);
            return ResponseResult.okResult(map);
        } else {
            // 2. 游客登录
            Map<String, Object> map = new HashMap<>();
            map.put("token", appJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }

    @Override
    public ResponseResult loginByCode(CodeLoginDto dto) {
        if (dto == null || !StringUtils.isNotBlank(dto.getPhone()))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 校验验证码
        if (!isVerified(dto.getPhone(), dto.getCode()))
            return ResponseResult.errorResult(AppHttpCodeEnum.VERIFY_CODE_INCORRECT);

        // 返回ApUser
        // 查询用户信息
        ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
        // 用户不存在
        if (dbUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户信息不存在");
        // 用户被ban
        if (dbUser.getStatus())
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_IS_BANNED);

        // 返回数据 jwt
        String token = appJwtUtil.getToken(dbUser.getId().longValue());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        dbUser.setSalt("");
        dbUser.setPassword("");
        map.put("user", dbUser);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult sendVerifyCode(String phone) {
        if (StringUtils.isEmpty(phone))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 过滤频繁请求
        String key = ApUserConstants.VERIFY_CODES + phone;
        Long expire = cacheService.getExpire(key, TimeUnit.MINUTES);
        if (expire - CODE_VALID_MINUTES < 1)
            return ResponseResult.errorResult(AppHttpCodeEnum.REQUEST_TOO_FREQUENT);

        // 生成五位随机数
        String code = Integer.toString(100000 + random.nextInt(900000));

        // 发送短信
        try {
            smsService.sendVerifyCode(phone, code);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        // 将验证码存入redis
        cacheService.setEx(key, code, CODE_VALID_MINUTES, TimeUnit.MINUTES);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult register(RegisterDto dto) {
        if (dto == null || StringUtils.isEmpty(dto.getName()) ||
                StringUtils.isEmpty(dto.getPhone()) || StringUtils.isEmpty(dto.getPassword()))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        // 检验手机号是否被注册
        ApUser existUser = getOne(new LambdaQueryWrapper<ApUser>()
                .select(ApUser::getPhone)
                .eq(ApUser::getPhone, dto.getPhone()));
        if (existUser != null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PHONE_EXIST);

        // 验证验证码是否有效
        if (!isVerified(dto.getPhone(), dto.getCode()))
            return ResponseResult.errorResult(AppHttpCodeEnum.VERIFY_CODE_INCORRECT);

        // 给密码加盐加密
        String salt = UUID.randomUUID().toString().replace("-", "");
        dto.setPassword(DigestUtils.md5DigestAsHex((dto.getPassword() + salt).getBytes()));

        ApUser apUser = new ApUser();
        BeanUtils.copyProperties(dto, apUser);
        apUser.setSalt(salt);
        apUser.setSex(ApUser.UNKNOWN);
        apUser.setIdentityAuthentication(false);
        apUser.setFlag(ApUser.NORMAL_USER);
        apUser.setDescription("这个人很懒，什么也没留下");
        apUser.setStatus(false);
        apUser.setImage(readPath + defaultUserImage);
        apUser.setCreatedTime(new Date());

        save(apUser);
//        log.info(readPath + defaultUserImage);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private Boolean isVerified(String phone, Integer code) {
        String dbCode = cacheService.get(ApUserConstants.VERIFY_CODES + phone);
        return dbCode != null && dbCode.equals(code.toString());
    }
}
