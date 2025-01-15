package com.heima.behavior.service;

import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ArticleBehaviorService {

    /**
     * 点赞行为
     * @param dto
     * @return
     */
    ResponseResult like(LikesBehaviorDto dto);

    /**
     * 不喜欢
     * @param dto
     * @return
     */
    ResponseResult dislike(DislikeBehaviorDto dto);

    /**
     * 阅读行为
     * @param dto
     * @return
     */
    ResponseResult read(ReadBehaviorDto dto);
}
