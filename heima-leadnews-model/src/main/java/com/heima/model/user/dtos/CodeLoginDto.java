package com.heima.model.user.dtos;

import lombok.Data;

@Data
public class CodeLoginDto {
    private String phone;
    private Integer code;
}
