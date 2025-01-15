package com.heima.apis.wemedia;

import com.heima.apis.wemedia.fallback.IWemediaUserClientFallback;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "leadnews-wemedia", fallback = IWemediaUserClientFallback.class)
public interface IWemediaUserClient {

    @PostMapping("/api/v1/wemedia/wm_user")
    ResponseResult getUserById(@RequestParam Integer id);
}
