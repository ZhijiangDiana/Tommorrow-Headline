package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:05:24
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {

    @Override
    public ResponseResult pageQuery(WmChannelDto wmChannelDto) {
        wmChannelDto.checkParam();

        IPage<WmChannel> pageRes = new Page<>();
        LambdaQueryWrapper<WmChannel> query = new LambdaQueryWrapper<WmChannel>()
                .orderByAsc(WmChannel::getOrd);
        if (StringUtils.isNotEmpty(wmChannelDto.getName()))
            query.like(WmChannel::getName, wmChannelDto.getName());
        if (wmChannelDto.getStatus() != null)
            query.eq(WmChannel::getStatus, wmChannelDto.getStatus());
        pageRes = page(pageRes, query);

        PageResponseResult res = new PageResponseResult(wmChannelDto.getPage(), wmChannelDto.getSize(), (int) pageRes.getTotal());
        res.setData(pageRes.getRecords());

        return res;
    }

    @Override
    public ResponseResult listAllChannels() {
        return ResponseResult.okResult(list(new LambdaQueryWrapper<WmChannel>()
                .eq(WmChannel::getStatus, (short) 1)
                .orderByAsc(WmChannel::getOrd)));
    }
}
