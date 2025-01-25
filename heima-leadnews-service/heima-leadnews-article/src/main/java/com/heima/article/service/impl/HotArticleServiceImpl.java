package com.heima.article.service.impl;

import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.common.redis.PipelineService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.HotArticleVO;
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
}
