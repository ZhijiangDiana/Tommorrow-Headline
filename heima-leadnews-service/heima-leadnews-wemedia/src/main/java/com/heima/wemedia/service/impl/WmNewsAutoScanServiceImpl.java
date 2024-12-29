package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.ImageModerationService;
import com.heima.common.aliyun.ModerationResult;
import com.heima.common.aliyun.TextModerationService;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/29-14:03:11
 */
@Service
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;
    
    @Autowired
    private TextModerationService textModerationService;
    
    @Autowired
    private ImageModerationService imageModerationService;
    
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private IArticleClient iArticleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    /**
     * 自媒体文章申鹤
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult autoScanWnNews(Integer id) {
        // 1.查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null)
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        if (Objects.equals(wmNews.getStatus(), WmNews.Status.SUBMIT.getCode())) {
            // 从内容中提取纯文本、内容和图片
            List<Map> contentJson = JSON.parseArray(wmNews.getContent(), Map.class);
            List<String> texts = contentJson.stream()
                    .filter(x -> StringUtils.isNotBlank((String) x.get("value")))
                    .filter(x -> "text".equals(x.get("type")))
                    .map(x -> (String) x.get("value"))
                    .collect(Collectors.toList());
            List<String> imgs = contentJson.stream()
                    .filter(x -> StringUtils.isNotBlank((String) x.get("value")))
                    .filter(x -> "image".equals(x.get("type")))
                    .map(x -> (String) x.get("value"))
                    .collect(Collectors.toList());
            String textToScan = wmNews.getTitle() + "\n" + StringUtils.join(texts, '\n');
            // 提取文章封面
            if (StringUtils.isNotBlank(wmNews.getImages())) {
                String[] covers = wmNews.getImages().split(",");
                imgs.addAll(Arrays.asList(covers));
            }
            imgs = imgs.stream().distinct().collect(Collectors.toList());

            // TODO 可以改为异步审核
            int flag = 0;
            String reason = "";
            try {
                Set<String> illegalLabel = new HashSet<>();
                // 2.申鹤文本内容
                ModerationResult textResult = new ModerationResult();
                List<ModerationResult> imagesResult = new ArrayList<>();
                if (StringUtils.isNotBlank(textToScan)) {
                    textResult = textModerationService.textJudge(textToScan);
                    if (ModerationResult.HIGH_RISK.equals(textResult.getRisk())) {
                        // 违规内容高风险
                        flag = 2;
                    } else if (ModerationResult.MEDIUM_RISK.equals(textResult.getRisk())) {
                        // 违规内容中风险
                        flag = 1;
                    }
                }

                // 3.申鹤图片
                if (!imgs.isEmpty()) {
                    for (String img : imgs) {
                        byte[] imgBytes = fileStorageService.downLoadFile(img);
                        ModerationResult result = imageModerationService.imageModeration(imgBytes);
                        imagesResult.add(result);
                        if (ModerationResult.HIGH_RISK.equals(textResult.getRisk())) {
                            // 违规内容高风险
                            flag = 2;
                        } else if (ModerationResult.MEDIUM_RISK.equals(textResult.getRisk())) {
                            // 违规内容中风险
                            flag = Math.max(flag, 1);
                        }
                    }
                }

                // 统一处理reason
                illegalLabel.addAll(textResult.getReason());
                imagesResult.stream().filter(Objects::nonNull).forEach(x -> illegalLabel.addAll(x.getReason()));
                reason = StringUtils.join(illegalLabel, ", ");
            } catch (Exception e) {
                e.printStackTrace();
                flag = 2;
                reason = AppHttpCodeEnum.SERVER_ERROR.getErrorMessage();
            }

            // 4.根据审核结果进行不同处理
            if (flag == 0) {
                // 申鹤成功，保存app端的相关文章数据
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
                wmNews.setStatus(WmNews.Status.SUCCESS.getCode());
                wmNews.setReason("审核成功");
                wmNews.setArticleId((Long) responseResult.getData());
                wmNewsMapper.updateById(wmNews);
            } else if (flag == 1) {
                // 审核失败，需要人工审核
                wmNews.setStatus(WmNews.Status.ADMIN_AUTH.getCode());
                wmNews.setReason(reason);
                wmNewsMapper.updateById(wmNews);
            } else {
                // 审核失败，不通过
                wmNews.setStatus(WmNews.Status.FAIL.getCode());
                wmNews.setReason(reason);
                wmNewsMapper.updateById(wmNews);
            }
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
