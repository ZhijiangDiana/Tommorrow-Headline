package com.heima.model.behavior.pojos;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * APP关注记录
 */
@Data
@Document("ap_user_behavior")
public class ApUserBehavior {

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
     * 1 关注
     * -1 取消关注
     */
    private Short behavior;

    @Transient
    public static final Short SUBSCRIBE_USER = 1;
    @Transient
    public static final Short CANCEL_SUBSCRIBE_USER = -1;

    /**
     * 被关注者userId
     */
    private Integer apUserId;

    /**
     * 被关注者wmId
     */
    private Integer wmUserId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

}