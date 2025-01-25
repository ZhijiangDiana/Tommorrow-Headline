package com.heima.apis.behavior.fallback;

import com.heima.apis.behavior.IBehaviorClient;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class IBehaviorClientFallback implements IBehaviorClient {
    @Override
    public ResponseResult likesBehavior(LikesBehaviorDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult readBehavior(ReadBehaviorDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult dislikeBehavior(DislikeBehaviorDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
