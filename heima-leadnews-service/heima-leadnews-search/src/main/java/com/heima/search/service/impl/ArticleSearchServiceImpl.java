package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.SearchConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.thread.ThreadLocalUtil;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApUserSearchService apUserSearchService;

    /**
     * ES文章分页搜索
     * @param userSearchDto
     * @return
     */
    @Override
    public ResponseResult search(UserSearchDto userSearchDto) throws IOException {
        // 1.检查参数
        if (userSearchDto == null || StringUtils.isEmpty(userSearchDto.getSearchWords()))
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        // 2.设置查询条件
        SearchRequest searchRequest = new SearchRequest(SearchConstants.AP_ARTICLE_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 关键字的分词之后查询
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(userSearchDto.getSearchWords())
                .field(SearchConstants.AP_ARTICLE_TITLE)
                .field(SearchConstants.AP_ARTICLE_CONTENT)
                .defaultOperator(Operator.OR);
        boolQueryBuilder.must(queryStringQueryBuilder);

        // 查询小于mindate的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(SearchConstants.AP_ARTICLE_PUBLISH_TIME)
                .lt(userSearchDto.getMinBehotTime().getTime());
        boolQueryBuilder.filter(rangeQueryBuilder);

        // 分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(userSearchDto.getPageSize());

        // 按照发布时间倒序查询
        searchSourceBuilder.sort(SearchConstants.AP_ARTICLE_PUBLISH_TIME, SortOrder.DESC);

        // 设置高亮title
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(SearchConstants.AP_ARTICLE_TITLE);
        highlightBuilder.preTags(SearchConstants.HIGHLIGHT_PRE_TAG);
        highlightBuilder.postTags(SearchConstants.HIGHLIGHT_POST_TAG);
        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 3.结果封装
        ArrayList<Map<String, String>> list = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Map<String, String> map = JSON.parseObject(json, Map.class);
            // 处理高亮
            if (hit.getHighlightFields() != null && !hit.getHighlightFields().isEmpty()) {
                Text[] titles = hit.getHighlightFields().get(SearchConstants.AP_ARTICLE_TITLE).getFragments();
                String title = StringUtils.join(titles);
                // 高亮标题
                map.put("h_title", title);
            } else {
                // 原始标题
                map.put("h_title", map.get(SearchConstants.AP_ARTICLE_TITLE));
            }
            list.add(map);
        }

        // 4.插入搜索记录
        ApUser user = (ApUser) ThreadLocalUtil.getObject();
        if (user != null && !user.getId().equals(0) && userSearchDto.getFromIndex() == 0)  // 不记录游客用户
            apUserSearchService.addSearchHistory(userSearchDto.getSearchWords(), user.getId());

        return ResponseResult.okResult(list);
    }
}
