package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:04:35
 */
public interface WmChannelService extends IService<WmChannel> {

    ResponseResult pageQuery(WmChannelDto wmChannelDto);
}
