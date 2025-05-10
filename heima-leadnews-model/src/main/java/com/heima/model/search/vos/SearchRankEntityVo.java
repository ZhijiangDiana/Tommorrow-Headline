package com.heima.model.search.vos;

import lombok.Data;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/10-15:39:07
 */
@Data
public class SearchRankEntityVo {
    private String searchWord;

    // 区分最近热点和历史热点
    private Short type;

    public static final Short HOT = 0;
    public static final Short RECOMMENDED = 1;
    public static final Short NEW = 2;
    public static final Short FIRE = 3;
    public static final Short ESSENCE = 4;
    public static final Short BRIGHT = 5;
    public static final Short NO_TAG = -1;
}
