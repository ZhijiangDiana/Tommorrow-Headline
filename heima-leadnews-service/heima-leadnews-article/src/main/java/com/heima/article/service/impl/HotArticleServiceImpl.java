package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.common.redis.PipelineService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.HotArticleVO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IWemediaClient wemediaClient;

    private static final int DATE_BACK = 30;

    private static final int CACHE_ARTICLE_CNT = 30;

    @Override
    public void syncArticleInfo() {
        // 1.查询所有被更改过的文章id
        Set<String> updatedKeys = cacheService.scan(BehaviorConstants.HAS_WROTE + "*").stream()
                .map(x -> x.substring(BehaviorConstants.HAS_WROTE.length()))
                .collect(Collectors.toSet());

        // 2.根据这些id查询其文章信息
        List<String> readKeys = updatedKeys.stream().map(x -> BehaviorConstants.ARTICLE_READ_COUNT + x).collect(Collectors.toList());
        List<String> likeKeys = updatedKeys.stream().map(x -> BehaviorConstants.ARTICLE_LIKE_CNT + x).collect(Collectors.toList());
        List<String> collectKeys = updatedKeys.stream().map(x -> BehaviorConstants.ARTICLE_COLLECT_CNT + x).collect(Collectors.toList());
        Map<String, String> reads = pipelineService.getKeyValueWithPipeline(readKeys);
        Map<String, String> likes = pipelineService.getKeyValueWithPipeline(likeKeys);
        Map<String, String> collects = pipelineService.getKeyValueWithPipeline(collectKeys);

        // 3.写入数据库
        for (String key : updatedKeys) {
            ApArticle apArticle = new ApArticle();
            apArticle.setId(Long.parseLong(key));
            apArticle.setViews(Integer.parseInt(reads.getOrDefault(BehaviorConstants.ARTICLE_READ_COUNT + key, "0")));
            apArticle.setLikes(Integer.parseInt(likes.getOrDefault(BehaviorConstants.ARTICLE_LIKE_CNT + key, "0")));
            apArticle.setCollection(Integer.parseInt(collects.getOrDefault(BehaviorConstants.ARTICLE_COLLECT_CNT + key, "0")));
            apArticleMapper.updateById(apArticle);
        }

        // 4.删除修改标记
        Long syncCnt = pipelineService.deleteWithPipeline(BehaviorConstants.HAS_WROTE + "*");
        log.info("已同步{}条文章信息数据到数据库", syncCnt);
    }

    /**
     * 计算热点文章
     */
    @Override
    public void computeHotArticle() {
        // 1.查询前五天的文章数据
        DateTime now = DateTime.now();
        Date to = now.withTimeAtStartOfDay().toDate();
        Date from = now.minusDays(DATE_BACK).withTimeAtStartOfDay().toDate();
        List<ApArticle> apArticles = apArticleMapper.findArticleListByLast5days(from, to);
        // 2.计算文章分值
        List<HotArticleVO> hotArticleVOS = new ArrayList<>();
        for (ApArticle apArticle : apArticles) {
            HotArticleVO hotArticleVO = new HotArticleVO();
            BeanUtils.copyProperties(apArticle, hotArticleVO);

            long score = 0;
            score += (long) apArticle.getViews() * ArticleConstants.HOT_ARTICLE_VIEW_WEIGHT;
            score += (long) apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
            score += (long) apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
            score += (long) apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
            hotArticleVO.setScore(score);

            hotArticleVOS.add(hotArticleVO);
        }

        // 3.为每个频道缓存30条分值较高的文章
        ResponseResult res = wemediaClient.listChannels();
        if (!Objects.equals(AppHttpCodeEnum.SUCCESS.getCode(), res.getCode()))
            throw new RuntimeException("远程调用失败...");
        List<WmChannel> wmChannels = JSON.parseArray(JSON.toJSONString(res.getData()), WmChannel.class);
        for (WmChannel wmChannel : wmChannels) {
            if (wmChannel.getStatus()) {
                List<HotArticleVO> channelHotArticles = hotArticleVOS.stream()
                        .filter(x -> x.getChannelId().equals(wmChannel.getId()))
                        .collect(Collectors.toList());
                sortAndCache(ArticleConstants.HOT_ARTICLE_FITST_PAGE + wmChannel.getId(), channelHotArticles);
            }
        }

        // 给所有文章排序并取30条数据
        sortAndCache(ArticleConstants.HOT_ARTICLE_FITST_PAGE + ArticleConstants.DEFAULT_TAG, hotArticleVOS);

    }

    private void sortAndCache(String key, List<HotArticleVO> channelHotArticles) {
        List<String> sortedArticles = channelHotArticles.stream()
                .sorted(Comparator.comparing(HotArticleVO::getScore).reversed())
                .limit(CACHE_ARTICLE_CNT)
                .map(JSON::toJSONString)
                .collect(Collectors.toList());

        cacheService.lRightPushAll(key, sortedArticles);
    }
}
