package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ApUserInfoController {

    @Autowired
    private ApUserCommonService apUserCommonService;

    @GetMapping("/info")
    public ResponseResult getUserInfo() {
        return apUserCommonService.getUserInfo();
    }
}
