package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-23:00:07
 */
@Data
public class WmMaterialDto extends PageRequestDto {
    /**
     * 1收藏  0未收藏
     */
    private Short isCollection;
}
