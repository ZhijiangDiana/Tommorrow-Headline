package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.wemedia.service.WmFansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/16-16:15:45
 */
@RestController
@RequestMapping("/api/v1/fans")
public class WmFansController {

    @Autowired
    private WmFansService wmFansService;

    @PostMapping("/page")
    public ResponseResult pageListFans(@RequestBody PageRequestDto pageRequestDto) {
        return wmFansService.pageListFans(pageRequestDto);
    }

    @PostMapping("/follow")
    public ResponseResult followFans(@RequestBody UserRelationDto userRelationDto) {
        return wmFansService.followOrUnFollowFans(userRelationDto);
    }
}
