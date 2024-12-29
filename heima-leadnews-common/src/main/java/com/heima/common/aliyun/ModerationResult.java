package com.heima.common.aliyun;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/28-23:48:02
 */
@Data
@ToString
public class ModerationResult {

    private String risk;
    private List<String> reason;

    public static final String NONE_RISK = "none";
    public static final String LOW_RISK = "low";
    public static final String MEDIUM_RISK = "medium";
    public static final String HIGH_RISK = "high";
}
