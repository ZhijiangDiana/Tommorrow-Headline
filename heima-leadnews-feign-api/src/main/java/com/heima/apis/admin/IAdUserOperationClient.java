package com.heima.apis.admin;

import com.heima.apis.admin.fallback.IAdUserOperationClientFallback;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(value = "leadnews-admin", fallback = IAdUserOperationClientFallback.class)
public interface IAdUserOperationClient {

    @PostMapping("/api/v1/adUserOperation/save")
    ResponseResult addAdUserOperation(AdUserOperaionDto adUserOperaionDto);
}
