package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class WmNewsAuthPageDto extends PageRequestDto {

    private Short status;
    private String title;
}
