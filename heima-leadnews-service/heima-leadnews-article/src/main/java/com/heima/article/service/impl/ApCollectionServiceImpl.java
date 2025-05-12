package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApCollectionService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleCollectionDto;
import com.heima.model.behavior.pojos.ApArticleBehavior;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApCollectionServiceImpl implements ApCollectionService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseResult apCollect(ArticleCollectionDto dto) {
        if (dto.getEntryId() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        String key1 = null, key2 = null;
        if (ArticleCollectionDto.ARTICLE_COLLECT_CODE.equals(dto.getType())) {
            key1 = BehaviorConstants.USER_ARTICLE_COLLECT;
            key2 = BehaviorConstants.ARTICLE_COLLECT_CNT;
        } else if (ArticleCollectionDto.MOMENT_COLLECT_CODE.equals(dto.getType())) {
            key1 = BehaviorConstants.USER_MOMENT_COLLECTION;
            key2 = BehaviorConstants.MOMENT_COLLECT_CNT;
        }
        key1 += userId.toString();
        key2 += dto.getEntryId().toString();

        // 组装实体
        Date now = new Date();
        ApArticleBehavior apArticleBehavior = new ApArticleBehavior();
        apArticleBehavior.setUserId(userId);
        apArticleBehavior.setArticleId(dto.getEntryId());
        apArticleBehavior.setType(dto.getType());

        //发送消息，数据聚合
        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getEntryId());
        mess.setType(UpdateArticleMess.UpdateArticleType.COLLECTION);

        if (ArticleCollectionDto.COLLECT.equals(dto.getOperation())) {
            Boolean isSucceed = cacheService.zAdd(key1, dto.getEntryId().toString(), System.currentTimeMillis());
            if (isSucceed) {
                // 执行收藏操作
                cacheService.incrBy(key2, 1);

                // 组装实体类
                apArticleBehavior.setBehavior(ApArticleBehavior.STAR_ARTICLE_BEHAVIOR);
                apArticleBehavior.setCreatedTime(now);
                apArticleBehavior.setUpdatedTime(now);

                // 将记录存入数据库
                mongoTemplate.save(apArticleBehavior);

                // 组装聚合实体
                mess.setAdd(1);
            }
        } else if (ArticleCollectionDto.UN_COLLECT.equals(dto.getOperation())) {
            Long removeCnt = cacheService.zRemove(key1, dto.getEntryId().toString());
            if (removeCnt > 0) {
                // 执行取消收藏操作
                cacheService.incrBy(key2, -1 * removeCnt);

                // 将收藏记录移除
                Query query = Query.query(Criteria
                        .where("articleId").is(dto.getEntryId())
                        .and("userId").is(userId)
                        .and("behavior").is(ApArticleBehavior.STAR_ARTICLE_BEHAVIOR)
                        .and("type").is(dto.getType()));
                mongoTemplate.remove(query, ApArticleBehavior.class);

                // 组装聚合实体
                mess.setAdd(-1);
            }
        }

        // 发送消息
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(mess));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
