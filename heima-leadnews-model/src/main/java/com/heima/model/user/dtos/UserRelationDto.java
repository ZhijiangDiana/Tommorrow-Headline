package com.heima.model.user.dtos;

import lombok.Data;

@Data
public class UserRelationDto {
    private Long articleId;
    private Integer authorId;
    private Integer operation;

    public static final Integer OPERATION_FOLLOW = 0;
    public static final Integer OPERATION_UN_FOLLOW = 1;
}
