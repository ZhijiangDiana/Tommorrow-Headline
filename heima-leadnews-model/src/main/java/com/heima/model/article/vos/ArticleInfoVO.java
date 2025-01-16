package com.heima.model.article.vos;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleInfoVO implements Serializable {

    /**
     * 点赞数据
     */
    private Boolean islike;
    private Integer likeCnt;

    /**
     * 是否已不喜欢
     */
    private Boolean isunlike;

    /**
     * 收藏数据
     */
    private Boolean iscollection;
    private Integer collectionCnt;

    /**
     * 关注数据
     */
    private Boolean isfollow;
    private Integer followCnt;
}
