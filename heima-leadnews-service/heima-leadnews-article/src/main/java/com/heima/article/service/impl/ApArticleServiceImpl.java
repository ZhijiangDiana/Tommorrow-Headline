package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/24-23:10:23
 */
@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

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
        if (StringUtils.isBlank(dto.getTag()))
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
    public ResponseResult saveArticle(ArticleDto dto) {
        // 1.检查参数
        if (dto == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        // 2.判断是否存在id
        if (dto.getId() == null) {
            // 2.1不存在id 保存文章、文章配置、文章内容
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
            // 修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(
                    new LambdaQueryWrapper<ApArticleContent>()
                    .eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        // 3.结果返回 返回文章id
        return ResponseResult.okResult(apArticle.getId());
    }
}
