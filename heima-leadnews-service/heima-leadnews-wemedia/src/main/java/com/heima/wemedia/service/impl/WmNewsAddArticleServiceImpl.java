package com.heima.wemedia.service.impl;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAddArticleService;
import com.heima.wemedia.service.WmNewsTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class WmNewsAddArticleServiceImpl implements WmNewsAddArticleService {

    @Autowired
    private IArticleClient iArticleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Lazy
    @Autowired
    private WmNewsTaskService wmNewsTaskService;

    @Override
    @Transactional
    public void autoSaveWmNews(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, articleDto);
        articleDto.setLayout(wmNews.getType());
        articleDto.setContent(wmNews.getContent());

        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmChannel != null) {
            articleDto.setChannelId(wmNews.getChannelId());
            articleDto.setChannelName(wmChannel.getName());
        }
        if (wmUser != null) {
            articleDto.setAuthorId(wmNews.getUserId().longValue());
            articleDto.setAuthorName(wmUser.getName());
        }

        if (wmNews.getArticleId() != null)
            articleDto.setId(wmNews.getArticleId());  // 有文章id，说明之前发布过，这次是修改
        else
            articleDto.setCreatedTime(new Date());  // 没有文章id，说明之前没有发布过，这次需要添加创建时间

        ResponseResult responseResult = iArticleClient.saveArticle(articleDto);
        if (!responseResult.getCode().equals(200))
            throw new RuntimeException(getClass().getSimpleName() + "-文章审核，保存app端相关文章数据失败");
        // 回填article_id
        if (wmNews.getPublishTime().after(new Date())) {
            // 若发布时间在当前时间之后，则添加定时任务发布
            wmNews.setStatus(WmNews.Status.SUCCESS.getCode());
            wmNewsTaskService.addPublishNewsTask(wmNews.getId(), wmNews.getPublishTime());
        } else {
            // 若发布时间在当前时间之前，则立即发布
            wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
        }
        wmNews.setReason("审核成功");
        wmNews.setArticleId((Long) responseResult.getData());
        wmNewsMapper.updateById(wmNews);
    }
}
