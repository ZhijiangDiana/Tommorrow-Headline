package com.heima.model.wemedia.vos;

import lombok.Data;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/15-11:28:27
 */
@Data
public class WmStatisticsNewsVO {

    private Integer wmNewsId;
    private Long articleId;
    private String title;
    private Integer readCnt;
    private Integer likeCnt;
    private Integer commentCnt;
    private Integer collectCnt;
    private Integer forwardCnt;
}
