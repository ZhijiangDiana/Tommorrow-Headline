package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:13:41
 */
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult listNews(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.pageListNews(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.submit(dto);
    }

    @GetMapping("/one/{id}")
    public ResponseResult getOneNews(@PathVariable Integer id) {
        return wmNewsService.getOneNews(id);
    }

    @GetMapping("/del_news/{nid}")
    public ResponseResult deleteNews(@PathVariable Integer nid) {
        return wmNewsService.deleteNews(nid);
    }

    @PostMapping("/down_or_up")
    public ResponseResult releaseOrPulledDown(@RequestBody WmNews wmNews) {
        return wmNewsService.upOrDown(wmNews.getId(), wmNews.getEnable());
    }
}
