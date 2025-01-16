package com.heima.model.behavior.dtos;

import lombok.Data;

@Data
public class DislikeBehaviorDto {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 操作    0 不喜欢    1 取消不喜欢
     */
    private Short type;
    public static final Short DISLIKE_OPERATION = 0;
    public static final Short DISCARD_DISLIKE_OPERATION = 1;
}
