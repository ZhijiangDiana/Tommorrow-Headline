package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.ThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:20:38
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Override
    public ResponseResult pageListNews(WmNewsPageReqDto dto) {
        dto.checkParam();
        WmUser wmUser = (WmUser) ThreadLocalUtil.getObject();
        if (wmUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> qw = new LambdaQueryWrapper<>();
        qw.eq(WmNews::getUserId, wmUser.getId())
                .orderByDesc(WmNews::getPublishTime);
        if (dto.getStatus() != null)
            qw.eq(WmNews::getStatus,dto.getStatus());
        if (dto.getBeginPubDate() != null)
            qw.ge(WmNews::getPublishTime, dto.getBeginPubDate());
        if (dto.getEndPubDate() != null)
            qw.le(WmNews::getPublishTime, dto.getEndPubDate());
        if (dto.getChannelId() != null)
            qw.eq(WmNews::getChannelId, dto.getChannelId());
        if (dto.getKeyword() != null)
            qw.like(WmNews::getTitle, dto.getKeyword());

        page = page(page, qw);
        ResponseResult res = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        res.setData(page.getRecords());
        return res;
    }
}
