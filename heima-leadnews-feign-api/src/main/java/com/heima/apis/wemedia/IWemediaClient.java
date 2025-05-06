package com.heima.apis.wemedia;

import com.heima.apis.wemedia.fallback.IWemediaClientFallback;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@FeignClient(value = "leadnews-wemedia", fallback = IWemediaClientFallback.class)
public interface IWemediaClient {

    /**
     * id查找用户
     * @param id
     * @return
     */
    @PostMapping("/api/v1/wemedia/wm_user")
    WmUser getUserById(@RequestParam Integer id);

    /**
     * 列出所有频道
     * @return
     */
    @PostMapping("/api/v1/channel/channels")
    ResponseResult listChannels();
}
