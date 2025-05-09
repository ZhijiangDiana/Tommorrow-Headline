package com.heima.search.service.impl;

import com.heima.common.constants.SearchConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.thread.ThreadLocalUtil;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CacheService cacheService;

    private static final Integer MAX_SEARCH_HISTORIES = 5;

    @Async
    @Override
    public void addSearchHistory(String keyword, Integer userId) {
//        Query query = Query.query(
//                Criteria.where("userId").is(userId)
//                        .and("keyword").is(keyword));
//        ApUserSearch apUserSearch = mongoTemplate.findOne(query, ApUserSearch.class);
//        if (apUserSearch == null) {
//            // 不存在历史记录，添加一个
//            apUserSearch = new ApUserSearch();
//            apUserSearch.setUserId(userId);
//            apUserSearch.setKeyword(keyword);
//            apUserSearch.setCreatedTime(new Date());
//            mongoTemplate.save(apUserSearch);
//            List<ApUserSearch> histories = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId))
//                    .with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);
//            if (histories.size() > MAX_SEARCH_HISTORIES) {
//                List<String> toBeRemoved = histories.subList(MAX_SEARCH_HISTORIES - 1, histories.size())
//                        .stream().map(ApUserSearch::getId).collect(Collectors.toList());
//                mongoTemplate.remove(Query.query(Criteria.where("id").in(toBeRemoved)), ApUserSearch.class);
//            }
//        } else {
//            // 存在历史记录，更新
//            apUserSearch.setCreatedTime(new Date());
//            mongoTemplate.save(apUserSearch);
//        }
        // 判断搜索词是否为空
        keyword = keyword.trim();
        if (StringUtil.isBlank(keyword))
            return;

        // 插入redis搜索记录
        String key = SearchConstants.USER_SEARCH_HISTORY + userId;
        cacheService.zAdd(key, keyword, System.currentTimeMillis());
        cacheService.zRemoveRange(key, 0, - MAX_SEARCH_HISTORIES - 1);

        // 向mongodb添加完整历史记录
        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keyword);
        apUserSearch.setCreatedTime(new Date());
        mongoTemplate.save(apUserSearch);
    }

    @Override
    public ResponseResult getSearchHistory() {
        Integer userId = ThreadLocalUtil.getUserId();
        if(userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

//        List<ApUserSearch> searchHistory = mongoTemplate.find(
//                Query.query(Criteria
//                        .where("userId").is(userId))
//                        .with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);
        // 查询搜索记录
        String key = SearchConstants.USER_SEARCH_HISTORY + userId;
        Set<String> histories = cacheService.zRangeAll(key);

        // 组装结果
        List<ApUserSearch> searchHistory = new ArrayList<>();
        for (String history : histories) {
            ApUserSearch apUserSearch = new ApUserSearch();
            apUserSearch.setUserId(userId);
            apUserSearch.setKeyword(history);
            searchHistory.add(apUserSearch);
        }
        Collections.reverse(searchHistory);

        return ResponseResult.okResult(searchHistory);
    }

    @Override
    public ResponseResult deleteSearchHistory(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), ApUserSearch.class);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
