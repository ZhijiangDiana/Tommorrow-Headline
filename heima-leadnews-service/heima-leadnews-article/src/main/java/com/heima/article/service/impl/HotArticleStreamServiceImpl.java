package com.heima.article.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleStreamService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.HotArticleVO;
import com.heima.model.mess.ArticleVisitStreamMess;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/12-02:39:50
 */
@Service
public class HotArticleStreamServiceImpl implements HotArticleStreamService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     * @param mess
     */
    @Override
    public void updateScore(ArticleVisitStreamMess mess) {
        //1.更新文章的阅读、点赞、收藏、评论的数量
        ApArticle apArticle = apArticleMapper.selectById(mess.getArticleId());
        updateArticle(apArticle, mess);
        //2.计算文章的分值
        Integer score = computeScore(apArticle);
        score = score * 3;

        //3.替换当前文章对应频道的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FITST_PAGE + apArticle.getChannelId());

        //4.替换总推荐对应的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FITST_PAGE + ArticleConstants.DEFAULT_TAG);

    }

    /**
     * 替换数据并且存入到redis
     * @param apArticle
     * @param score
     */
    private void replaceDataToRedis(ApArticle apArticle, Integer score, String key) {
        // 缓存中存在，更新分值
        List<HotArticleVO> rank = cacheService.zRangeAll(key).stream().map(x -> JSON.parseObject(x, HotArticleVO.class)).collect(Collectors.toList());

        for (HotArticleVO article : rank) {
            if (article.getId().equals(apArticle.getId())) {
                cacheService.zRemoveRangeByScore(key, article.getScore(), article.getScore());
                break;
            }
        }

        cacheService.zAdd(key, JSON.toJSONString(apArticle), score);

//        // 限制总量最大不超过30
//        Long count = cacheService.zCount(key, 0, Double.MAX_VALUE);
//        if (count > ArticleConstants.HOT_CACHE_ARTICLE_CNT)
//            cacheService.zRemoveRange(key, 0, 0);

    }

    /**
     * 更新文章行为数量
     * @param mess
     */
    private ApArticle updateArticle(ApArticle apArticle, ArticleVisitStreamMess mess) {
        apArticle.setCollection(apArticle.getCollection()==null?0:apArticle.getCollection()+mess.getCollect());
        apArticle.setComment(apArticle.getComment()==null?0:apArticle.getComment()+mess.getComment());
        apArticle.setLikes(apArticle.getLikes()==null?0:apArticle.getLikes()+mess.getLike());
        apArticle.setViews(apArticle.getViews()==null?0:apArticle.getViews()+mess.getView());
        apArticleMapper.updateById(apArticle);

        return apArticle;
    }

    /**
     * 计算文章的具体分值
     * @param apArticle
     * @return
     */
    private Integer computeScore(ApArticle apArticle) {
        Integer score = 0;
        if(apArticle.getLikes() != null){
            score += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if(apArticle.getViews() != null){
            score += apArticle.getViews() * ArticleConstants.HOT_ARTICLE_VIEW_WEIGHT;
        }
        if(apArticle.getComment() != null){
            score += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if(apArticle.getCollection() != null){
            score += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return score;
    }
}
