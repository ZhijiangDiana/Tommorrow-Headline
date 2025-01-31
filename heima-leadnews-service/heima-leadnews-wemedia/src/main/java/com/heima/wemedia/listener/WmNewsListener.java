package com.heima.wemedia.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.service.WmNewsAddArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WmNewsListener {

    @Autowired
    private WmNewsAddArticleService wmNewsAddArticleService;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_SAVE_TOPIC)
    public void wmNewsSave(String message) {
        if (message != null) {
            WmNews wmNews = JSON.parseObject(message, WmNews.class);
            boolean isPublish = wmNews.getId() == null;
            wmNewsAddArticleService.autoSaveWmNews(wmNews);
            log.info("id={}文章已{}成功", wmNews.getId(), isPublish ? "发布" : "修改");
        }
    }
}
