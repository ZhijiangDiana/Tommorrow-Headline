package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.ThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/27-03:20:38
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Override
    public ResponseResult pageListNews(WmNewsPageReqDto dto) {
        dto.checkParam();
        WmUser wmUser = (WmUser) ThreadLocalUtil.getObject();
        if (wmUser == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);

        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> qw = new LambdaQueryWrapper<>();
        qw.eq(WmNews::getUserId, wmUser.getId())
                .orderByDesc(WmNews::getPublishTime);
        if (dto.getStatus() != null)
            qw.eq(WmNews::getStatus,dto.getStatus());
        if (dto.getBeginPubDate() != null)
            qw.ge(WmNews::getPublishTime, dto.getBeginPubDate());
        if (dto.getEndPubDate() != null)
            qw.le(WmNews::getPublishTime, dto.getEndPubDate());
        if (dto.getChannelId() != null)
            qw.eq(WmNews::getChannelId, dto.getChannelId());
        if (dto.getKeyword() != null)
            qw.like(WmNews::getTitle, dto.getKeyword());

        page = page(page, qw);
        ResponseResult res = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        res.setData(page.getRecords());
        return res;
    }

    @Override
    @Transactional
    public ResponseResult submit(WmNewsDto dto) {
        // 0.条件判断
        if (dto == null || dto.getContent() == null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);


        // 1.保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            String imgStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imgStr);
        }
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO))
            wmNews.setType(null);
        WmUser wmUser = (WmUser) ThreadLocalUtil.getObject();
        wmNews.setUserId(wmUser.getId());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable(WemediaConstants.WM_NEWS_RELEASED);  // 默认上架
        if (dto.getId() != null) {
            // 修改
            // 删除文章图片和文章的关联
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery()
                    .eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        } else {
            // 发布
            wmNews.setCreatedTime(new Date());
            save(wmNews);
            dto.setId(wmNews.getId());
        }


        // 2.判断是否为草稿  如果为草稿结束当前方法
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode()))
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);


        // 3.不是草稿，保存文章内容图片与素材的关系
        //获取到文章内容中的图片信息
        List<String> materials = new ArrayList<>();
        List<Map> tokens = JSON.parseArray(dto.getContent(), Map.class);
        for (Map token : tokens) {
            if (token.get("type").equals("image")) {
                String imgUrl = (String) token.get("value");
                materials.add(imgUrl);
            }
        }
        // 保存文章图片与素材的关系到数据库中
        // 通过图片url查询图片id
        saveRelativeInfo(materials, dto.getId(), WemediaConstants.WM_CONTENT_REFERENCE);


        // 4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        List<String> imgs = dto.getImages();
        // 自动模式
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            if (materials.size() >= 3) {
                // 多图
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                imgs = imgs.stream().limit(3).collect(Collectors.toList());
            } else if (!materials.isEmpty()) {
                // 单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                imgs = imgs.stream().limit(1).collect(Collectors.toList());
            } else {
                // 无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            // 修改文章封面图片
            if (imgs != null && !imgs.isEmpty())
                wmNews.setImages(StringUtils.join(imgs, ","));
            updateById(wmNews);
        }
        // 将封面图的引用关系写入数据库
        if (imgs != null && !imgs.isEmpty())
            saveRelativeInfo(imgs, dto.getId(), WemediaConstants.WM_COVER_REFERENCE);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(new LambdaQueryWrapper<WmMaterial>()
                .select(WmMaterial::getId)
                .in(WmMaterial::getUrl, materials));
        if (dbMaterials == null || dbMaterials.isEmpty() || dbMaterials.size() != materials.size())
            throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
        // 收集id
        List<Integer> ids = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
        // 批量保存
        wmNewsMaterialMapper.saveRelations(ids, newsId, type);
    }
}
