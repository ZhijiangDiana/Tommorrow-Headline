package com.heima.model.common.enums;

import lombok.Getter;

@Getter
public enum AdminOperationEnum {

    /**
     * 用户类
     */
    LOGIN(0, "登录"),


    /**
     * 敏感词管理
     */
    ADD_SENSITIVES(1000, "添加敏感词"),
    REMOVE_SENSITIVES(1001, "删除敏感词"),
    MODIFY_SENSITIVES(1002, "修改敏感词");

    /**
     * 频道管理
     */



    private int type;
    private String description;

    AdminOperationEnum(int type, String description){
        this.type = type;
        this.description = description;
    }

}
