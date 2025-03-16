package com.heima.model.behavior.pojos;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * APP文章记录
 */
@Data
@Document("ap_article_behavior")
public class ApArticleBehavior {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户行为
     * 0 阅读文章
     * 1 点赞文章
     * 2 收藏文章
     * 3 不喜欢文章
     * 4 转发文章
     * 5 评论文章
     */
    private Short behavior;

    @Transient
    public static final Short READ_ARTICLE_BEHAVIOR = 0;
    @Transient
    public static final Short LIKE_ARTICLE_BEHAVIOR = 1;
    @Transient
    public static final Short STAR_ARTICLE_BEHAVIOR = 2;
    @Transient
    public static final Short DISLIKE_ARTICLE_BEHAVIOR = 3;
    @Transient
    public static final Short FORWARD_ARTICLE_BEHAVIOR = 4;
    @Transient
    public static final Short COMMENT_ARTICLE_BEHAVIOR = 5;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 类型    0 文章    1 动态    评论
     */
    private Short type;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

}