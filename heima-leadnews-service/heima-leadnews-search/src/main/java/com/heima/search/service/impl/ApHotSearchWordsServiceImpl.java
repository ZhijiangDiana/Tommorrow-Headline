package com.heima.search.service.impl;

import com.heima.common.constants.SearchConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.pojos.ApUserSearch;
import com.heima.model.search.vos.SearchRankEntityVo;
import com.heima.search.service.ApHotSearchWordsService;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/5/10-15:29:23
 */
@Service
public class ApHotSearchWordsServiceImpl implements ApHotSearchWordsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CacheService cacheService;

    private static final int OFFLINE_PAGE_SIZE = 10;

    private static final int ONLINE_PAGE_SIZE = 0;

    private static final int MIN_WORD_COUNT = 10;
    private static final int STORAGE_MAX_LIMIT = 20;

    private Random random = new Random();

    @Override
    public ResponseResult getHotSearchWords() {
        List<String> offlineResult = cacheService.lRange(SearchConstants.OFFLINE_HOT_SEARCH_RANK, 0, OFFLINE_PAGE_SIZE - 1);
        List<String> onlineResult = cacheService.lRange(SearchConstants.ONLINE_HOT_SEARCH_RANK, 0, ONLINE_PAGE_SIZE);

        List<SearchRankEntityVo> res = new ArrayList<>();
        // 组装实时结果实体
        for (String result : onlineResult) {
            SearchRankEntityVo vo = new SearchRankEntityVo();
            vo.setSearchWord(result);
            vo.setType(SearchRankEntityVo.NEW);
            res.add(vo);
        }
        // 组装离线结果实体
        for (String result : offlineResult) {
            SearchRankEntityVo vo = new SearchRankEntityVo();
            vo.setSearchWord(result);
            vo.setType(SearchRankEntityVo.NO_TAG);
            res.add(vo);
        }
        if (ONLINE_PAGE_SIZE < offlineResult.size())
            res.get(ONLINE_PAGE_SIZE).setType(SearchRankEntityVo.HOT);
        if (ONLINE_PAGE_SIZE + 1 < offlineResult.size())
            res.get(ONLINE_PAGE_SIZE + 1).setType(SearchRankEntityVo.HOT);
        if (ONLINE_PAGE_SIZE + 2 < offlineResult.size())
            res.get(ONLINE_PAGE_SIZE + 2).setType(SearchRankEntityVo.RECOMMENDED);

        return ResponseResult.okResult(res);
    }

    @Override
    public void calculateHotSearchWords() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        List<KeywordCount> res = getKeywordCounts(yesterday, true);
        if (res.size() < MIN_WORD_COUNT)
            res = getKeywordCounts(yesterday, false);
        List<String> store = res.stream().map(KeywordCount::getId).collect(Collectors.toList());

        // 删除旧记录
        cacheService.delete(SearchConstants.OFFLINE_HOT_SEARCH_RANK);
        // 存储新记录
        cacheService.lRightPushAll(SearchConstants.OFFLINE_HOT_SEARCH_RANK, store);
    }

    private List<KeywordCount> getKeywordCounts(LocalDateTime yesterday, boolean enableLimit) {
        // 使用Aggregation管道聚合
        GroupOperation group = Aggregation.group("keyword").count().as("count");
        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.DESC, "count"));
        Aggregation aggregation;
        if (enableLimit) {
            MatchOperation match = Aggregation.match(Criteria.where("createdTime").gte(yesterday));
            LimitOperation limit = Aggregation.limit(STORAGE_MAX_LIMIT);  // 限制前20条
            aggregation = Aggregation.newAggregation(match, group, sort, limit);
        }  else {
            aggregation = Aggregation.newAggregation(group, sort);
        }

        // 获取热词结果
        List<KeywordCount> res = mongoTemplate.aggregate(aggregation, "ap_user_search", KeywordCount.class)
                .getMappedResults();
        return res;
    }

    @Data
    @ToString
    private static class KeywordCount {
        private String id;  // 这里的 id 是 keyword
        private long count;
    }
}
