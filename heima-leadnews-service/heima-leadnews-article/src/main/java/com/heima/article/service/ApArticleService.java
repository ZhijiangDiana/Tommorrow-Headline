package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/24-23:09:55
 */
public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     * @param dto
     * @param type
     * @return 1 加载更多     2 加载最新
     */
    ResponseResult load(ArticleHomeDto dto, Short type);

    /**
     * 保存app端相关文章
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDto dto);

    /**
     * 加载文章点赞收藏等数据
     * @param dto
     * @return
     */
    ResponseResult loadInfo(ArticleInfoDto dto);

    /**
     * 加载文章列表
     * @param dto
     * @param type
     * @param firstPage
     * @return
     */
    ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage);
}
