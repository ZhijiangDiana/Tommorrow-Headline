package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmStatisticsDto;


public interface WmStatisticsService {

    /**
     * 图文统计总览
     * @param dto
     * @return
     */
    ResponseResult newsDimension(WmStatisticsDto dto);

    /**
     * 分页查询图文统计
     * @return
     */
    ResponseResult newsPage(WmStatisticsDto dto);

    /**
     * 具体文章统计
     * @param dto
     * @return
     */
    ResponseResult newsPortrait(WmStatisticsDto dto);
}
