package com.heima.apis.wemedia.fallback;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IWemediaClientFallback implements IWemediaClient {

    @Override
    public WmUser getUserById(Integer id) {
        return null;
    }

    @Override
    public ResponseResult listChannels() {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
