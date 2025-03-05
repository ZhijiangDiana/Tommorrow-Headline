package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApHistoryStarArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleHistoryDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.thread.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApHistoryStarArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApHistoryStarArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private CacheService cacheService;

    private final static short MAX_PAGE_SIZE = 50;
    private final static int DEFAULT_PAGE_SIZE = 10;

    @Override
    public ResponseResult load(ArticleHistoryDto dto) {
        // 1、校验参数
        // size
        Integer size = dto.getSize();
        if (size == null || size == 0)
            dto.setSize(DEFAULT_PAGE_SIZE);
        dto.setSize(Math.min(size, MAX_PAGE_SIZE));

        // 检查登录
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        // 频道参数
        if (StringUtils.isEmpty(dto.getTag()))
            dto.setTag(ArticleConstants.DEFAULT_TAG);

        // 2、查询
        List<ZSetOperations.TypedTuple<String>> history = null;
        if (dto.getType().equals(ArticleHistoryDto.LOAD_HISTORY)) {
            // 历史记录
//            log.info("最大时间：{}", dto.getMaxBehotTime().getTime());
            history = new ArrayList<>(
                    cacheService.zRangeByScoreWithScores(
                            BehaviorConstants.USER_ARTICLE_READ + userId,
                            0,
                            dto.getMaxBehotTime().getTime() - 1)
            );// 减一以防止新批次的第一项与上批次的最后一项重复
        } else if (dto.getType().equals(ArticleHistoryDto.LOAD_STAR)) {
            // 加载收藏
            history = new ArrayList<>(
                    cacheService.zRangeByScoreWithScores(
                            BehaviorConstants.USER_ARTICLE_COLLECT + userId,
                            0,
                            dto.getMaxBehotTime().getTime() - 1)
            );// 减一以防止新批次的第一项与上批次的最后一项重复
        }

        Collections.reverse(history);
        history = history.stream()
                .limit(dto.getSize())
                .collect(Collectors.toList());

        // 组装结果
        List<ApArticle> apArticles = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : history) {
            ApArticle apArticle = apArticleMapper.selectById(Long.parseLong(tuple.getValue()));
            ApArticleConfig apArticleConfig = apArticleConfigMapper.selectOne(new LambdaQueryWrapper<ApArticleConfig>()
                    .eq(ApArticleConfig::getArticleId, apArticle.getId()));
            if (apArticleConfig == null || apArticleConfig.getIsDown() || apArticleConfig.getIsDelete()) {
                apArticle.setId(null);
                apArticle.setTitle("文章不可见");
                apArticle.setComment(null);
                apArticle.setImages("");
                apArticle.setAuthorId(null);
                apArticle.setAuthorName("");
                apArticle.setStaticUrl("");
            }
            apArticle.setPublishTime(new Date(tuple.getScore().longValue()));

            apArticles.add(apArticle);
        }

        // 3、返回
        return ResponseResult.okResult(apArticles);
    }
}
