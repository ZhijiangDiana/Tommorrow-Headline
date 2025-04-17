package com.heima.model.user.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/4/17-23:39:33
 */
@Data
public class ApRealnameDto extends PageRequestDto {
    private Integer id;
    private String msg;
    private Short status;
}
