package com.heima.behavior.service.impl;

import com.heima.behavior.service.ArticleBehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.dtos.DislikeBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.pojos.ApArticleBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ArticleBehaviorServiceImpl implements ArticleBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MongoTemplate mongoTemplate;

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

        // 写入修改标记
        cacheService.set(BehaviorConstants.HAS_WROTE + articleIdString, articleIdString);

        // 组装实体
        Date now = new Date();
        ApArticleBehavior apArticleBehavior = new ApArticleBehavior();
        apArticleBehavior.setUserId(userId);
        apArticleBehavior.setArticleId(dto.getArticleId());
        apArticleBehavior.setType(dto.getType());

        // 点赞或取消点赞
        if (LikesBehaviorDto.LIKE_OPERATION.equals(dto.getOperation())) {
            // 文章点赞数据存入数据库
            Boolean isSuccess = cacheService.zAdd(setKey2, articleIdString, now.getTime());
            if (isSuccess) {
                // 执行点赞操作
                cacheService.incrBy(setKey1, 1);

                // 组装实体类
                apArticleBehavior.setBehavior(ApArticleBehavior.LIKE_ARTICLE_BEHAVIOR);
                apArticleBehavior.setCreatedTime(now);
                apArticleBehavior.setUpdatedTime(now);

                // 将记录存入数据库
                mongoTemplate.save(apArticleBehavior);
            }
        } else if (LikesBehaviorDto.DISCARD_LIKE_OPERATION.equals(dto.getOperation())) {
            // 文章点赞数据删除
            Long removeCnt = cacheService.zRemove(setKey2, articleIdString);
            if (removeCnt > 0) {
                // 执行取消点赞操作
                cacheService.incrBy(setKey1, -1 * removeCnt);

                // 将点赞记录移除
                Query query = Query.query(Criteria
                        .where("articleId").is(dto.getArticleId())
                        .and("userId").is(userId)
                        .and("behavior").is(ApArticleBehavior.LIKE_ARTICLE_BEHAVIOR)
                        .and("type").is(dto.getType()));
                mongoTemplate.remove(query, ApArticleBehavior.class);
            }
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

        // 写入修改标记
        cacheService.set(BehaviorConstants.HAS_WROTE + articleIdString, articleIdString);

        // 组装实体
        Date now = new Date();
        ApArticleBehavior apArticleBehavior = new ApArticleBehavior();
        apArticleBehavior.setUserId(userId);
        apArticleBehavior.setArticleId(dto.getArticleId());
        apArticleBehavior.setType(dto.getType());

        if (DislikeBehaviorDto.DISLIKE_OPERATION.equals(dto.getType())) {
            Boolean isSuccess = cacheService.zAdd(key2, articleIdString, now.getTime());
            if (isSuccess) {
                // 执行不喜欢操作
                cacheService.incrBy(key1, 1);

                // 组装实体类
                apArticleBehavior.setBehavior(ApArticleBehavior.DISLIKE_ARTICLE_BEHAVIOR);
                apArticleBehavior.setCreatedTime(now);
                apArticleBehavior.setUpdatedTime(now);

                // 将记录存入数据库
                mongoTemplate.save(apArticleBehavior);
            }
        } else if (DislikeBehaviorDto.DISCARD_DISLIKE_OPERATION.equals(dto.getType())) {
            Long removeCnt = cacheService.zRemove(key2, articleIdString);
            if (removeCnt > 0) {
                // 执行取消不喜欢操作
                cacheService.incrBy(key1, -1 * removeCnt);

                // 将不喜欢记录移除
                Query query = Query.query(Criteria
                        .where("articleId").is(dto.getArticleId())
                        .and("userId").is(userId)
                        .and("behavior").is(ApArticleBehavior.DISLIKE_ARTICLE_BEHAVIOR));
                mongoTemplate.remove(query, ApArticleBehavior.class);
            }
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

        // 写入修改标记
        cacheService.set(BehaviorConstants.HAS_WROTE + articleIdString, articleIdString);
        // 存入数据库
        cacheService.incrBy(articleKey, dto.getCount());
        cacheService.zRemove(userKey, articleIdString);
        cacheService.zAdd(userKey, articleIdString, System.currentTimeMillis());

        // 组装实体
        Date now = new Date();
        ApArticleBehavior apArticleBehavior = new ApArticleBehavior();
        apArticleBehavior.setUserId(userId);
        apArticleBehavior.setArticleId(dto.getArticleId());
        apArticleBehavior.setBehavior(ApArticleBehavior.READ_ARTICLE_BEHAVIOR);
        apArticleBehavior.setCreatedTime(now);
        apArticleBehavior.setUpdatedTime(now);

        // 将记录存入数据库
        mongoTemplate.save(apArticleBehavior);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult startRead(Long articleId) {
        if (articleId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 将阅读记录插入redis
        String key = BehaviorConstants.USER_ARTICLE_START_READ_TIME + userId;
        cacheService.zAdd(key, articleId.toString(), System.currentTimeMillis());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult stopRead(Long articleId) {
        if (articleId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 查询并计算阅读时长
        String key = BehaviorConstants.USER_ARTICLE_START_READ_TIME + userId;
        Long startTime = cacheService.zScore(key, articleId.toString()).longValue();
        long endTime = System.currentTimeMillis();
        Long readTime = endTime - startTime;

        // 删除开始阅读记录
        cacheService.zRemove(key, articleId.toString());

        // 将阅读时长写入redis
        String readTimeKey = BehaviorConstants.USER_READ_TIME + userId;
        cacheService.zAdd(readTimeKey, readTime.toString(), startTime);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
