package com.heima.wemedia.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WemediaClient implements IWemediaClient {

    @Autowired
    private WmUserService wmUserService;

    @Autowired
    private WmChannelService wmChannelService;

    @Override
    @PostMapping("/api/v1/wemedia/wm_user")
    public ResponseResult getUserById(Integer id) {
        return ResponseResult.okResult(wmUserService.getOne(new LambdaQueryWrapper<WmUser>().eq(WmUser::getApUserId, id)));
    }

    @Override
    @PostMapping("/api/v1/channel/channels")
    public ResponseResult listChannels() {
        return wmChannelService.listAllChannels();
    }
}
