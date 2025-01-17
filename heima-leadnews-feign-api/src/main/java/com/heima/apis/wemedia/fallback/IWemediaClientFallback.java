package com.heima.apis.wemedia.fallback;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class IWemediaClientFallback implements IWemediaClient {

    @Override
    public ResponseResult getUserById(Integer id) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult listChannels() {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
