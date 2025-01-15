package com.heima.behavior.service.impl;

import com.heima.behavior.service.ArticleBehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleBehaviorServiceImpl implements ArticleBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult like(LikesBehaviorDto dto) {
        if (dto.getArticleId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        long incr = 0;
        if (BehaviorConstants.LIKE_OPERATION.equals(dto.getOperation()))
            incr = 1;
        else if (BehaviorConstants.DISCARD_LIKE_OPERATION.equals(dto.getOperation()))
            incr = -1;

        String key = null;
        if (BehaviorConstants.ARTICLE_LIKE_CODE.equals(dto.getType()))
            key = BehaviorConstants.ARTICLE_LIKE;
        else if (BehaviorConstants.MOMENT_LIKE_CODE.equals(dto.getType()))
            key = BehaviorConstants.MOMENT_LIKE;
        else if (BehaviorConstants.COMMENT_LIKE_CODE.equals(dto.getType()))
            key = BehaviorConstants.COMMENT_LIKE;
        key += String.valueOf(dto.getArticleId());

        cacheService.setIfAbsent(key, "0");
        cacheService.incrBy(key, incr);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult dislike(DislikeBehaviorDto dto) {
        if (dto.getArticleId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        long incr = 0;
        if (BehaviorConstants.DISLIKE_OPERATION.equals(dto.getType()))
            incr = 1;
        else if (BehaviorConstants.DISCARD_DISLIKE_OPERATION.equals(dto.getType()))
            incr = -1;

        String key = BehaviorConstants.ARTICLE_DISLIKE + dto.getArticleId();
        cacheService.setIfAbsent(key, "0");
        cacheService.incrBy(key, incr);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult read(ReadBehaviorDto dto) {
        if (dto.getArticleId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        String key = BehaviorConstants.ARTICLE_READ + dto.getArticleId();
        cacheService.setIfAbsent(key, "0");
        cacheService.incrBy(key, dto.getCount());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
