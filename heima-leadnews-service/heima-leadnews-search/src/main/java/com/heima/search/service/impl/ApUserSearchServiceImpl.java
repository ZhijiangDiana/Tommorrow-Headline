package com.heima.search.service.impl;

import com.heima.model.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Integer MAX_SEARCH_HISTORIES = 10;

    @Async
    @Override
    public void addSearchHistory(String keyword, Integer userId) {
        Query query = Query.query(
                Criteria.where("userId").is(userId)
                        .and("keyword").is(keyword));
        ApUserSearch apUserSearch = mongoTemplate.findOne(query, ApUserSearch.class);
        if (apUserSearch == null) {
            // 不存在历史记录，添加一个
            apUserSearch = new ApUserSearch();
            apUserSearch.setUserId(userId);
            apUserSearch.setKeyword(keyword);
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
            List<ApUserSearch> histories = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId))
                    .with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);
            if (histories.size() > MAX_SEARCH_HISTORIES) {
                List<String> toBeRemoved = histories.subList(MAX_SEARCH_HISTORIES - 1, histories.size())
                        .stream().map(ApUserSearch::getId).collect(Collectors.toList());
                mongoTemplate.remove(Query.query(Criteria.where("id").in(toBeRemoved)), ApUserSearch.class);
            }
        } else {
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
        }
    }
}
