package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.wemedia.dtos.WmNewsEnableDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ArticleConfigListener {

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void onUpOrDownMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            WmNewsEnableDto wmNewsEnableDto = JSON.parseObject(message, WmNewsEnableDto.class);
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId(wmNewsEnableDto.getArticleId());
            apArticleConfig.setIsDown(Short.valueOf((short) 0).equals(wmNewsEnableDto.getEnable()));
            apArticleConfigService.update(apArticleConfig, new LambdaQueryWrapper<ApArticleConfig>()
                    .eq(ApArticleConfig::getArticleId, apArticleConfig.getArticleId()));
        }
    }

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_DELETE_TOPIC)
    public void onDeleteMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            Long aid = Long.parseLong(message);
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId(aid);
            apArticleConfig.setIsDelete(true);
            apArticleConfigService.update(apArticleConfig, new LambdaUpdateWrapper<ApArticleConfig>()
                    .eq(ApArticleConfig::getArticleId, aid));
        }
    }
}
