package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.utils.thread.ThreadLocalUtil;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {

    @Override
    public ResponseResult deleteSensitive(Integer id) {
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public PageResponseResult pageQuery(WmSensitiveDto dto) {
        dto.checkParam();

        IPage<WmSensitive> pageQuery = new Page<>(dto.getPage(), dto.getSize());
        pageQuery = page(pageQuery, new LambdaQueryWrapper<WmSensitive>()
                .like(WmSensitive::getSensitives, dto.getName())
                .orderByDesc(WmSensitive::getCreatedTime));
        PageResponseResult res = new PageResponseResult(dto.getPage(), dto.getSize(), (int) pageQuery.getTotal());
        res.setData(pageQuery.getRecords());
        return res;
    }

    @Override
    public ResponseResult insertSensitive(WmSensitive wmSensitive) {
        if (StringUtils.isEmpty(wmSensitive.getSensitives()))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        wmSensitive.setId(null);
        wmSensitive.setCreatedTime(new Date());
        save(wmSensitive);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult updateSensitive(WmSensitive wmSensitive) {
        updateById(wmSensitive);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
