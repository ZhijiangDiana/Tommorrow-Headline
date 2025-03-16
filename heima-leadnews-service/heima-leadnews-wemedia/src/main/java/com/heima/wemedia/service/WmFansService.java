package com.heima.wemedia.service;

import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import org.springframework.data.domain.PageRequest;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/16-14:47:17
 */
public interface WmFansService {

    ResponseResult pageListFans(PageRequestDto pageRequestDto);

    ResponseResult followOrUnFollowFans(UserRelationDto userRelationDto);
}
