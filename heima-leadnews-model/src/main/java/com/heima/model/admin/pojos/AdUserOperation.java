package com.heima.model.admin.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员登录记录表
 */
@Data
@TableName("ad_user_operation")
public class AdUserOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 操作设备号
     */
    @TableField("equipment_id")
    private Integer equipmentId;

    /**
     * 执行操作的ip
     */
    @TableField("ip")
    private String ip;

    /**
     * 执行操作的地址
     */
    @TableField("address")
    private String address;

    /**
     * 操作类型
     */
    @TableField("type")
    private Short type;

    /**
     * 操作描述
     */
    @TableField("description")
    private String description;

    /**
     * 操作时间
     */
    @TableField("created_time")
    private Date createdTime;
}
