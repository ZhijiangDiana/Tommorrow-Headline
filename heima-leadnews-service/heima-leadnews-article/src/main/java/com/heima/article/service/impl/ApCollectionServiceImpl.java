package com.heima.article.service.impl;

import com.heima.article.service.ApCollectionService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleCollectionDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApCollectionServiceImpl implements ApCollectionService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult apCollect(ArticleCollectionDto dto) {
        if (dto.getEntryId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        String key1 = null, key2 = null;
        if (ArticleCollectionDto.ARTICLE_COLLECT_CODE.equals(dto.getType())) {
            key1 = BehaviorConstants.ARTICLE_COLLECTION;
            key2 = BehaviorConstants.ARTICLE_BE_COLLECTED;
        } else if (ArticleCollectionDto.MOMENT_COLLECT_CODE.equals(dto.getType())) {
            key1 = BehaviorConstants.MOMENT_COLLECTION;
            key2 = BehaviorConstants.MOMENT_BE_COLLECTED;
        }
        key1 += userId.toString();
        key2 += dto.getEntryId().toString();

        if (ArticleCollectionDto.COLLECT.equals(dto.getOperation())) {
            cacheService.zAdd(key1, dto.getEntryId().toString(), System.currentTimeMillis());
            cacheService.zAdd(key2, userId.toString(), System.currentTimeMillis());
        } else if (ArticleCollectionDto.UN_COLLECT.equals(dto.getOperation())) {
            cacheService.zRemove(key1, dto.getEntryId().toString());
            cacheService.zRemove(key2, userId.toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
