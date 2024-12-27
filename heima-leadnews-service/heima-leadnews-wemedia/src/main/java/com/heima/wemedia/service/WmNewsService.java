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

    ResponseResult pageListNews(WmNewsPageReqDto dto);

    ResponseResult submit(WmNewsDto dto);
}
