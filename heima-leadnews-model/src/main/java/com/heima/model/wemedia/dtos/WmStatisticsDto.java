package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/15-11:16:52
 */
@Data
public class WmStatisticsDto extends PageRequestDto {

    private Long beginDate;
    private Long endDate;
    private Integer articleId;
}

