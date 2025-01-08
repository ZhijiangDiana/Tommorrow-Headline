package com.heima.model.wemedia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WmNewsEnableDto {

    private Long articleId;
    private Short enable;
}
