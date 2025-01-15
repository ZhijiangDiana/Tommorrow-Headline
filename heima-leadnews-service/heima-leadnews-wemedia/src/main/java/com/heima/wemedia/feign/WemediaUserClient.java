package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IWemediaUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WemediaUserClient implements IWemediaUserClient {

    @Autowired
    private WmUserService wmUserService;

    @Override
    @PostMapping("/api/v1/wemedia/wm_user")
    public ResponseResult getUserById(Integer id) {
        return ResponseResult.okResult(wmUserService.getById(id));
    }
}
