package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.search.vos.SearchArticleVo;
import com.heima.model.wemedia.dtos.WmNewsEnableDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
public class ArticleConfigListener {

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void onUpOrDownMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            // 修改文章状态
            WmNewsEnableDto wmNewsEnableDto = JSON.parseObject(message, WmNewsEnableDto.class);
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId(wmNewsEnableDto.getArticleId());
            apArticleConfig.setIsDown(Short.valueOf((short) 0).equals(wmNewsEnableDto.getEnable()));
            apArticleConfigService.update(apArticleConfig, new LambdaQueryWrapper<ApArticleConfig>()
                    .eq(ApArticleConfig::getArticleId, apArticleConfig.getArticleId()));

            // 修改文章修改时间
            ApArticle apArticle = apArticleMapper.selectById(wmNewsEnableDto.getArticleId());
            apArticle.setUpdatedTime(new Date());
            apArticleMapper.updateById(apArticle);

            // 修改文章索引
            if (wmNewsEnableDto.getEnable().equals((short) 1)) {
                ApArticleContent apArticleContent = apArticleContentMapper.selectOne(
                        new LambdaQueryWrapper<ApArticleContent>()
                        .eq(ApArticleContent::getArticleId, apArticleConfig.getArticleId()));
                SearchArticleVo searchArticleVo = new SearchArticleVo();
                BeanUtils.copyProperties(apArticle, searchArticleVo);
                searchArticleVo.setContent(apArticleContent.getContent());
                kafkaTemplate.send(ArticleConstants.ARTICLE_ADD_INDEX_TOPIC, JSON.toJSONString(searchArticleVo));
            } else if (wmNewsEnableDto.getEnable().equals((short) 0)) {
                kafkaTemplate.send(ArticleConstants.ARTICLE_REMOVE_INDEX_TOPIC, wmNewsEnableDto.getArticleId().toString());
            }
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
            // 修改文章修改时间
            ApArticle apArticle = apArticleMapper.selectById(aid);
            apArticle.setUpdatedTime(new Date());
            apArticleMapper.updateById(apArticle);
        }
    }
}
