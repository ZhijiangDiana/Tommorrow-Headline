package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.pojos.ApArticleBehavior;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmStatisticsDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.WmStatisticsNewsVO;
import com.heima.model.wemedia.vos.WmStatisticsOverallVO;
import com.heima.utils.thread.ThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description 
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/14-23:10:41
 */
@Slf4j
@Service
public class WmStatisticsServiceImpl implements WmStatisticsService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WmNewsMapper wmNewsMapper;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public ResponseResult newsDimension(WmStatisticsDto dto) {
        // 校验请求参数
        if (dto.getBeginDate() == null)
            dto.setBeginDate(0L);
        if (dto.getEndDate() == null)
            dto.setEndDate(System.currentTimeMillis());

        Date from = new Date(dto.getBeginDate());
        Date to = new Date(dto.getEndDate());
        log.info("统计时间段：{} ~ {}", sdf.format(from), sdf.format(to));

        // 登录验证
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 获取已发布的文章id
        List<Long> articleIds = wmNewsMapper.selectList(new LambdaQueryWrapper<WmNews>()
                        .eq(WmNews::getStatus, WmNews.Status.PUBLISHED.getCode())
//                        .eq(WmNews::getEnable, WmNews.WM_NEWS_ENABLE)
                        .eq(WmNews::getUserId, userId)
                        .select(WmNews::getArticleId))
                        .stream()
                        .filter(Objects::nonNull)
                        .map(WmNews::getArticleId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        log.info("articleIds: {}", articleIds);
        
        // 获取原始数据
        Integer newsPublishedCnt = wmNewsMapper.selectCount(new LambdaQueryWrapper<WmNews>()
                .eq(WmNews::getStatus, WmNews.Status.PUBLISHED.getCode())
                .between(WmNews::getPublishTime, from, to));

        Query likesQuery = Query.query(Criteria
                .where("articleId").in(articleIds)
                .and("createdTime").gt(from).lte(to)
                .and("behavior").is(ApArticleBehavior.LIKE_ARTICLE_BEHAVIOR));
        Integer newsLikesCnt = (int) mongoTemplate.count(likesQuery, ApArticleBehavior.class);

        Query collectQuery = Query.query(Criteria
                .where("articleId").in(articleIds)
                .and("createdTime").gt(from).lte(to)
                .and("behavior").is(ApArticleBehavior.STAR_ARTICLE_BEHAVIOR));
        Integer newsCollectCnt = (int) mongoTemplate.count(collectQuery, ApArticleBehavior.class);

        Integer newsForwardCnt = 0;

        // 组装vo
        WmStatisticsOverallVO wmStatisticsOverallVO = new WmStatisticsOverallVO();
        wmStatisticsOverallVO.setNewsPublishCnt(newsPublishedCnt);
        wmStatisticsOverallVO.setNewsLikeCnt(newsLikesCnt);
        wmStatisticsOverallVO.setNewsCollectCnt(newsCollectCnt);
        wmStatisticsOverallVO.setNewsForwardCnt(newsForwardCnt);

        return ResponseResult.okResult(wmStatisticsOverallVO);
    }

    @Override
    public ResponseResult newsPage(WmStatisticsDto dto) {
        // 校验请求参数
        if (dto.getBeginDate() == null)
            dto.setBeginDate(0L);
        if (dto.getEndDate() == null)
            dto.setEndDate(System.currentTimeMillis());

        Date from = new Date(dto.getBeginDate());
        Date to = new Date(dto.getEndDate());
        log.info("统计时间段：{} ~ {}", sdf.format(from), sdf.format(to));

        // 登录验证
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 整页查询
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        page = wmNewsMapper.selectPage(page, new LambdaQueryWrapper<WmNews>()
                .eq(WmNews::getUserId, userId)
                .eq(WmNews::getStatus, WmNews.Status.PUBLISHED.getCode())
//                    .eq(WmNews::getEnable, WmNews.WM_NEWS_ENABLE)
                .between(WmNews::getPublishTime, from, to)
                .select(WmNews::getId, WmNews::getArticleId, WmNews::getTitle)
                .orderByDesc(WmNews::getPublishTime));

        // 准备数据
        List<WmStatisticsNewsVO> record = new ArrayList<>();
        for (WmNews wmNews : page.getRecords()) {
            if (wmNews == null)
                continue;

            WmStatisticsNewsVO resVO = new WmStatisticsNewsVO();
            BeanUtils.copyProperties(wmNews, resVO);
            resVO.setWmNewsId(wmNews.getId());

            // 阅读计数
            Query readQuery = Query.query(Criteria
                    .where("articleId").is(resVO.getArticleId())
                    .and("behavior").is(ApArticleBehavior.READ_ARTICLE_BEHAVIOR));
            resVO.setReadCnt((int) mongoTemplate.count(readQuery, ApArticleBehavior.class));

            // 点赞计数
            Query likeQuery = Query.query(Criteria
                    .where("articleId").is(resVO.getArticleId())
                    .and("behavior").is(ApArticleBehavior.LIKE_ARTICLE_BEHAVIOR));
            resVO.setLikeCnt((int) mongoTemplate.count(likeQuery, ApArticleBehavior.class));

            // 评论计数
            Query commentQuery = Query.query(Criteria
                    .where("articleId").is(resVO.getArticleId())
                    .and("behavior").is(ApArticleBehavior.COMMENT_ARTICLE_BEHAVIOR));
            resVO.setCommentCnt((int) mongoTemplate.count(commentQuery, ApArticleBehavior.class));

            // 收藏计数
            Query collectQuery = Query.query(Criteria
                    .where("articleId").is(resVO.getArticleId())
                    .and("behavior").is(ApArticleBehavior.STAR_ARTICLE_BEHAVIOR));
            resVO.setCollectCnt((int) mongoTemplate.count(collectQuery, ApArticleBehavior.class));

            // 转发计数
            Query forwardQuery = Query.query(Criteria
                    .where("articleId").is(resVO.getArticleId())
                    .and("behavior").is(ApArticleBehavior.FORWARD_ARTICLE_BEHAVIOR));
            resVO.setForwardCnt((int) mongoTemplate.count(forwardQuery, ApArticleBehavior.class));

            record.add(resVO);
        }

        ResponseResult res = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        res.setData(record);
        return res;
    }

    @Override
    public ResponseResult newsPortrait(WmStatisticsDto dto) {
        return null;
    }
}
