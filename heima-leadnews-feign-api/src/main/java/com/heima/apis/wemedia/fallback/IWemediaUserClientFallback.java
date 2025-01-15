package com.heima.apis.wemedia.fallback;

import com.heima.apis.wemedia.IWemediaUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class IWemediaUserClientFallback implements IWemediaUserClient {

    @Override
    public ResponseResult getUserById(Integer id) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
