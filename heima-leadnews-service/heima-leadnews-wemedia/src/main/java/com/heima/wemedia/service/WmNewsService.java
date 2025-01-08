package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:19:42
 */
public interface WmNewsService extends IService<WmNews> {

    /**
     * 条件查询文章列表
     * @param dto
     * @return
     */
    ResponseResult pageListNews(WmNewsPageReqDto dto);

    /**
     * 发布修改文章或保存为草稿
     * @param dto
     * @return
     */
    ResponseResult submit(WmNewsDto dto);

    /**
     * 获取一个文章
     * @param nid
     * @return
     */
    ResponseResult getOneNews(Integer nid);

    /**
     * 删除文章
     * @param nid
     * @return
     */
    ResponseResult deleteNews(Integer nid);

    /**
     * 上架或下架文章
     * @param id
     * @param enable
     * @return
     */
    ResponseResult upOrDown(Integer id, Short enable);
}
