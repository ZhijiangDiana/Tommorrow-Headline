package com.heima.admin.controller.v1;

import com.heima.admin.service.AdUserService;
import com.heima.model.admin.dtos.LoginDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/login")
@Api(value = "admin端用户登录", tags = "admin端用户登录")
public class AdUserLoginController {

    @Autowired
    private AdUserService adUserService;

    @PostMapping("/in")
    @ApiOperation("管理员用户登录")
    public ResponseResult login(HttpServletRequest request, @RequestBody LoginDto dto) {
        String ipAddress = request.getRemoteAddr();
        if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
            ipAddress = "127.0.0.1";
        }
        log.info(ipAddress);
        return adUserService.login(dto);
    }
}
