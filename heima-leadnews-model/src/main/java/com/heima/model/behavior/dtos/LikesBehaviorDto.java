package com.heima.model.behavior.dtos;

import lombok.Data;

@Data
public class LikesBehaviorDto {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 操作    0 点赞    1 取消点赞
     */
    private Short operation;
    public static final Short LIKE_OPERATION = 0;
    public static final Short DISCARD_LIKE_OPERATION = 1;

    /**
     * 类型    0 文章    1 动态    评论
     */
    private Short type;
    public static final Short ARTICLE_LIKE_CODE = 0;
    public static final Short MOMENT_LIKE_CODE = 1;
    public static final Short COMMENT_LIKE_CODE = 2;
}
