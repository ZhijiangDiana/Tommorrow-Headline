package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/7-01:41:16
 */
@Data
public class ApUserProfileDto {
    private Integer userId;
    private String name;
    private String password;
    private String phone;
    private String email;
    private String image;
    private String description;
    private Short sex;
}
