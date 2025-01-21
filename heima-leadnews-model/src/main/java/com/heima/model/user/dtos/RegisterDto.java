package com.heima.model.user.dtos;

import lombok.Data;

@Data
public class RegisterDto {
    private String phone;
    private String name;
    private String password;
    private Integer code;
}
