package com.heima.apis.admin.fallback;

import com.heima.apis.admin.IAdUserOperationClient;
import com.heima.model.admin.dtos.AdUserOperaionDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;


@Component
public class IAdUserOperationClientFallback implements IAdUserOperationClient {

    @Override
    public ResponseResult addAdUserOperation(AdUserOperaionDto adUserOperaionDto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
