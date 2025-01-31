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
import com.heima.wemedia.utils.ACAutomation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {

    @Autowired
    private ACAutomation acAutomation;

    @Override
    public ResponseResult deleteSensitive(Integer id) {
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public PageResponseResult pageQuery(WmSensitiveDto dto) {
        dto.checkParam();

        IPage<WmSensitive> pageQuery = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmSensitive> query = new LambdaQueryWrapper<WmSensitive>()
                .orderByDesc(WmSensitive::getCreatedTime);
        if (StringUtils.isNotEmpty(dto.getName()))
            query.like(WmSensitive::getSensitives, dto.getName());
        pageQuery = page(pageQuery, query);
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

    @Async
    @PostConstruct
    @Override
    public void reloadAcAutomation() {
        List<String> sensitives = list(new LambdaQueryWrapper<WmSensitive>()
                .select(WmSensitive::getSensitives))
                .stream()
                .map(WmSensitive::getSensitives)
                .collect(Collectors.toList());
        acAutomation.reload(sensitives);
        log.info(">>>>>>>>>> 重启AC自动机完成");
    }
}
