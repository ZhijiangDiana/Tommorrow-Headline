package com.heima.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskTypeEnum {

    NEWS_SCAN(1001, 1,"文章定时审核"),
    NEWS_PUBLISH(1002, 1, "文章定时发布"),
    REMOTE_ERROR(9999, 2,"第三方接口调用失败，重试");
    private final int taskType; //对应具体业务
    private final int priority; //业务不同级别
    private final String desc; //描述信息
}