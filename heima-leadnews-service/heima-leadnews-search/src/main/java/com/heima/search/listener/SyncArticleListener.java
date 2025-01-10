package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.SearchConstants;
import com.heima.model.search.vos.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SyncArticleListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @KafkaListener(topics = ArticleConstants.ARTICLE_ADD_INDEX_TOPIC)
    public void addNewIndex(String message) throws IOException {
        if (StringUtils.isNotBlank(message)) {
            SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);
            IndexRequest indexRequest = new IndexRequest(SearchConstants.AP_ARTICLE_INDEX);
            indexRequest.id(searchArticleVo.getId().toString());
            indexRequest.source(message, XContentType.JSON);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @KafkaListener(topics = ArticleConstants.ARTICLE_REMOVE_INDEX_TOPIC)
    public void removeIndex(String articleId) throws IOException {
        if (StringUtils.isNotBlank(articleId)) {
            DeleteRequest deleteRequest = new DeleteRequest(SearchConstants.AP_ARTICLE_INDEX, articleId);
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        }
    }

}
