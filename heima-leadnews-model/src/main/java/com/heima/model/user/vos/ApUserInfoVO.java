package com.heima.model.user.vos;

import lombok.Data;

@Data
public class ApUserInfoVO {

    private String name;
    private String avatar;
    private Boolean verified;
    private String readingTime;
    private Integer posts;
    private Integer following;
    private Integer followers;
}
