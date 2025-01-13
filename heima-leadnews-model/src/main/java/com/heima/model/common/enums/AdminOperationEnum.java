package com.heima.model.common.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum AdminOperationEnum implements Serializable {

    /**
     * 用户类
     */
    LOGIN(0, "登录"),


    /**
     * 敏感词管理
     */
    ADD_SENSITIVES(1000, "添加敏感词"),
    REMOVE_SENSITIVES(1001, "删除敏感词"),
    MODIFY_SENSITIVES(1002, "修改敏感词"),

    /**
     * 频道管理
     */
    ADD_CHANNEL(1010, "添加频道"),
    REMOVE_CHANNEL(1011, "删除频道"),
    MODIFY_CHANNEL(1012, "修改频道");



    private int type;
    private String description;

    AdminOperationEnum(int type, String description){
        this.type = type;
        this.description = description;
    }

}
