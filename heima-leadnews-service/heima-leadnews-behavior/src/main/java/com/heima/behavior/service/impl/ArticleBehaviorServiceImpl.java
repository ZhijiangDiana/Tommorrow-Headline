package com.heima.behavior.service.impl;

import com.heima.behavior.service.ArticleBehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.thread.ThreadLocalUtil;
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
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // setKey1表示文章视角看，文章的各项数据
        // setKey2表示用户视角看，用户的各项数据
        String setKey1 = null, setKey2 = null;
        if (LikesBehaviorDto.ARTICLE_LIKE_CODE.equals(dto.getType())) {
            setKey1 = BehaviorConstants.ARTICLE_LIKE_CNT;
            setKey2 = BehaviorConstants.USER_ARTICLE_LIKE;
        } else if (LikesBehaviorDto.MOMENT_LIKE_CODE.equals(dto.getType())) {
            setKey1 = BehaviorConstants.MOMENT_LIKE_CNT;
            setKey2 = BehaviorConstants.USER_MOMENT_LIKE;
        } else if (LikesBehaviorDto.COMMENT_LIKE_CODE.equals(dto.getType())) {
            setKey1 = BehaviorConstants.COMMENT_LIKE_CNT;
            setKey2 = BehaviorConstants.USER_COMMENT_LIKE;
        }
        String articleIdString = String.valueOf(dto.getArticleId());
        String userIdString = String.valueOf(userId);
        setKey1 += articleIdString;
        setKey2 += userIdString;

        long now = System.currentTimeMillis();
        if (LikesBehaviorDto.LIKE_OPERATION.equals(dto.getOperation())) {
            // 文章点赞数据存入数据库
            Boolean isSuccess = cacheService.zAdd(setKey2, articleIdString, now);
            if (isSuccess)
                cacheService.incrBy(setKey1, 1);
        } else if (LikesBehaviorDto.DISCARD_LIKE_OPERATION.equals(dto.getOperation())) {
            // 文章点赞数据删除
            Long removeCnt = cacheService.zRemove(setKey2, articleIdString);
            if (removeCnt > 0)
                cacheService.incrBy(setKey1, -1 * removeCnt);
        }


        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult dislike(DislikeBehaviorDto dto) {
        if (dto.getArticleId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        String articleIdString = dto.getArticleId().toString();
        String userIdString = String.valueOf(userId);
        // setKey1表示文章视角看，文章的各项数据
        String key1 = BehaviorConstants.ARTICLE_DISLIKE_CNT + articleIdString;
        // setKey2表示用户视角看，用户的各项数据
        String key2 = BehaviorConstants.USER_ARTICLE_DISLIKE + userIdString;

        long now = System.currentTimeMillis();
        if (DislikeBehaviorDto.DISLIKE_OPERATION.equals(dto.getType())) {
            Boolean isSuccess = cacheService.zAdd(key2, articleIdString, now);
            if (isSuccess)
                cacheService.incrBy(key1, 1);
        } else if (DislikeBehaviorDto.DISCARD_DISLIKE_OPERATION.equals(dto.getType())) {
            Long removeCnt = cacheService.zRemove(key2, articleIdString);
            if (removeCnt > 0)
                cacheService.incrBy(key1, -1 * removeCnt);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult read(ReadBehaviorDto dto) {
        if (dto.getArticleId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        String articleIdString = dto.getArticleId().toString();
        String userIdString = String.valueOf(userId);
        // articleKey表示文章的阅读量
        String articleKey = BehaviorConstants.ARTICLE_READ_COUNT + articleIdString;
        // userKey表示用户阅读的所有文章集合
        String userKey = BehaviorConstants.USER_ARTICLE_READ + userIdString;

        // 存入数据库
        cacheService.incrBy(articleKey, dto.getCount());
        cacheService.zAdd(userKey, articleIdString, System.currentTimeMillis());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
