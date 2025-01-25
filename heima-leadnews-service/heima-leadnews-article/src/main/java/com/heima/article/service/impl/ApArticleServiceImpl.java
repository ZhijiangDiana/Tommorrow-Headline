package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vos.ArticleInfoVO;
import com.heima.model.article.vos.HotArticleVO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.vos.SearchArticleVo;
import com.heima.utils.thread.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/24-23:10:23
 */
@Service
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final static short MAX_PAGE_SIZE = 50;
    private final static int DEFAULT_PAGE_SIZE = 10;

    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        // 1、校验参数
        // size
        Integer size = dto.getSize();
        if (size == null || size == 0)
            dto.setSize(DEFAULT_PAGE_SIZE);
        dto.setSize(Math.min(size, MAX_PAGE_SIZE));

        // type
        if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) &&
                !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW))
            type = ArticleConstants.LOADTYPE_LOAD_MORE;

        // 频道参数
        if (StringUtils.isEmpty(dto.getTag()))
            dto.setTag(ArticleConstants.DEFAULT_TAG);

        // 时间校验
        if (dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if (dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());


        // 2、查询
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, type);

        // 3、返回
        return ResponseResult.okResult(apArticles);
    }

    @Override
    @Transactional
    public ResponseResult saveArticle(ArticleDto dto) {

//        // 取消注释以下代码可测试服务熔断
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        // 1.检查参数
        if (dto == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        // 2.判断是否存在id
        if (dto.getId() == null) {
            // 2.1不存在id 保存文章、文章配置、文章内容
            apArticle.setViews(0);
            apArticle.setLikes(0);
            apArticle.setCollection(0);
            apArticle.setComment(0);
            // 保存文章
            save(apArticle);
            // 保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);
            // 保存文章内容
            ApArticleContent content = new ApArticleContent();
            content.setArticleId(apArticle.getId());
            content.setContent(dto.getContent());
            apArticleContentMapper.insert(content);
        } else {
            // 2.2存在id 修改文章、文章内容
            // 修改文章
            updateById(apArticle);
            // 修改文章配置
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setIsDown(false);
            apArticleConfig.setIsDelete(false);
            apArticleConfigMapper.update(apArticleConfig, new LambdaQueryWrapper<ApArticleConfig>()
                    .eq(ApArticleConfig::getArticleId, dto.getId()));
            // 修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(
                    new LambdaQueryWrapper<ApArticleContent>()
                    .eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        // 异步调用生成静态文件并上传到minio中
        articleFreemarkerService.buildArticleToMinio(apArticle, dto.getContent());

        // 发送消息通知搜索服务
        apArticle = apArticleMapper.selectById(apArticle.getId());
        SearchArticleVo searchArticleVo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle, searchArticleVo);
        searchArticleVo.setContent(dto.getContent());
        kafkaTemplate.send(ArticleConstants.ARTICLE_ADD_INDEX_TOPIC, JSON.toJSONString(searchArticleVo));

        // 3.结果返回 返回文章id
        return ResponseResult.okResult(apArticle.getId());
    }

    @Override
    public ResponseResult loadInfo(ArticleInfoDto dto) {
        Long articleId = dto.getArticleId();
        Integer authorId = dto.getAuthorId();
        if (articleId == null || authorId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        Integer userId = ThreadLocalUtil.getUserId();
        if (userId == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        String userIdString = userId.toString();
        String authorIdString = authorId.toString();
        String articleIdString = articleId.toString();
        ArticleInfoVO articleInfoVO = new ArticleInfoVO();
        // 用户是否已点赞
        articleInfoVO.setIslike(cacheService.zScore(BehaviorConstants.USER_ARTICLE_LIKE + userIdString, articleIdString) != null);
        // 文章被点赞次数
        String likeCntStr = cacheService.get(BehaviorConstants.ARTICLE_LIKE_CNT + articleIdString);
        articleInfoVO.setLikeCnt(likeCntStr == null ? 0 : Integer.parseInt(likeCntStr));
        // 用户是否已不喜欢
        articleInfoVO.setIsunlike(cacheService.zScore(BehaviorConstants.USER_ARTICLE_DISLIKE + userIdString, articleIdString) != null);
        // 用户是否已关注
        articleInfoVO.setIsfollow(cacheService.zScore(BehaviorConstants.FOLLOW_LIST + userIdString, authorIdString) != null);
        // 作者粉丝数
        Long followCntStr = cacheService.zSize(BehaviorConstants.FAN_LIST + authorIdString);
        articleInfoVO.setFollowCnt(followCntStr == null ? 0 : followCntStr.intValue());
        // 用户是否已收藏
        articleInfoVO.setIscollection(cacheService.zScore(BehaviorConstants.USER_ARTICLE_COLLECT + userIdString, articleIdString) != null);
        // 文章收藏数
        String collectCntStr = cacheService.get(BehaviorConstants.ARTICLE_COLLECT_CNT + articleIdString);
        articleInfoVO.setCollectionCnt(collectCntStr == null ? 0 : Integer.parseInt(collectCntStr));

        return ResponseResult.okResult(articleInfoVO);
    }

    @Override
    public ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage) {
        /**
         * 规则：
         * 1. 若为firstpage，则将redis的热门文章取回后打乱，取前x条返回
         * 2. 若不是firstpage，则从数据库选出n条数据，再从redis中取m条数据，打乱后返回（热门数据条数的期望是nm/(n+m)）
         */
        List<String> hotArticleStr = cacheService.lRange(
                ArticleConstants.HOT_ARTICLE_FITST_PAGE + dto.getTag(),
                0, ArticleConstants.HOT_CACHE_ARTICLE_CNT);
        Collections.shuffle(hotArticleStr);
        if (firstPage) {
            List<HotArticleVO> res = hotArticleStr.stream()
                    .limit(ArticleConstants.FIRST_PAGE_ARTICLE_CNT)
                    .map(x -> JSON.parseObject(x, HotArticleVO.class))
                    .collect(Collectors.toList());

            return ResponseResult.okResult(res);
        } else {
            int hotArticleCnt = (int) Math.round(dto.getSize() * ArticleConstants.LOADMORE_HOT_ARTICLE_RATIO);

            List<HotArticleVO> newArticles = (List<HotArticleVO>) load(dto, type).getData();
            List<HotArticleVO> hotArticles = hotArticleStr.stream()
                    .distinct()
                    .limit(hotArticleCnt)
                    .map(x -> JSON.parseObject(x, HotArticleVO.class))
                    .collect(Collectors.toList());
            newArticles.addAll(hotArticles);
            Collections.shuffle(newArticles);
            List<HotArticleVO> res = newArticles.subList(0, Math.min(dto.getSize(), newArticles.size()));

            return ResponseResult.okResult(res);
        }
    }
}
