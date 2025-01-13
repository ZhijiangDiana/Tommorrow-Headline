package com.heima.admin.feign;

import com.heima.admin.service.AdUserOperationService;
import com.heima.apis.admin.IAdUserOperationClient;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("leadnews-admin")
public class AdUserOperationClient implements IAdUserOperationClient {

    @Autowired
    private AdUserOperationService adUserOperationService;

    @Override
    @PostMapping("/api/v1/adUserOperation/save")
    public ResponseResult addAdUserOperation(@RequestBody AdUserOperaionDto adUserOperaionDto) {
        return adUserOperationService.recordOperation(adUserOperaionDto);
    }
}
