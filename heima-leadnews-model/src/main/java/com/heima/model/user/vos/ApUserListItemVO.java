package com.heima.model.user.vos;

import lombok.Data;

import java.util.Date;

@Data
public class ApUserListItemVO {

    private String name;
    private String description;
    private String avatar;
    private Boolean isFollowing;
    private Boolean isMutual;
    private Date followDate;
}
