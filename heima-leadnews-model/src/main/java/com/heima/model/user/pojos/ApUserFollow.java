package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApUserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 被关注自媒体人id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 关注者（粉丝）id
     */
    @TableField("follow_id")
    private Integer followId;

    /**
     * 关注者昵称
     */
    @TableField("follow_name")
    private String followName;

    /**
     * 关注度
     * 0 偶尔感兴趣    1 一般    2 经常    3 高度
     */
    @TableField("level")
    private Short level;

    public static final Short LEVEL_SOMETIMES = 0;
    public static final Short LEVEL_AVERAGE = 1;
    public static final Short LEVEL_OFTEN = 2;
    public static final Short LEVEL_HIGH = 3;

    /**
     * 是否动态通知
     */
    @TableField("is_notice")
    private Short isNotice;

    /**
     * 关注时间
     */
    @TableField("created_time")
    private Date createdTime;
}
