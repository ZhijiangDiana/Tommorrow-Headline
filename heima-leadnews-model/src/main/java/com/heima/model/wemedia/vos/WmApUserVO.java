package com.heima.model.wemedia.vos;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/16-15:32:37
 */
@Data
public class WmApUserVO {

    private Integer id;
    private String name;
    private String image;
    private String description;
    private String sex;
    private Date followedTime;
    private Boolean isFollowing;
}
