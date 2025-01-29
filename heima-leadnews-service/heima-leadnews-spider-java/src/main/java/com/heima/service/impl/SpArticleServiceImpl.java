package com.heima.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.apis.article.IArticleClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.spider.dto.ArticleJsonInsertDto;
import com.heima.service.SpArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class SpArticleServiceImpl implements SpArticleService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private IArticleClient articleClient;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Async
    @Override
    public void spiderBatchInsert(ArticleJsonInsertDto info) throws IOException, ParseException {
        if (StringUtils.isEmpty(info.getSearchWord()) || StringUtils.isEmpty(info.getChannelName()) || info.getChannelId() == null || info.getBasePath() == null)
            log.error("参数缺失");

        String basePath = info.getBasePath();
        basePath += File.separator + info.getSearchWord();

        InputStreamReader inputStreamReader = new InputStreamReader(
                Files.newInputStream(Paths.get(basePath + File.separator + "article_final_" + info.getSearchWord() + ".json")));
        String jsonStr = IOUtils.toString(inputStreamReader);
        inputStreamReader.close();
        JSONArray articles = JSON.parseArray(jsonStr);
        // 去重set
        Set<String> distinctTitle = new HashSet<>();
//        System.out.println(articles.get(0).toString());
        for (int i = 0; i < articles.size(); i++) {
            JSONObject article = articles.getJSONObject(i);

            // 若输入中含有重复的标题，则跳过
            boolean isUnique = distinctTitle.add(article.getString("title"));
            if (!isUnique) {
                log.error("index={}文章本次已插入，已跳过", i);
                continue;
            }

            // 若数据库已有相同文章，则跳过
            ApArticle apArticle = new ApArticle();
            apArticle.setTitle(article.getString("title"));
            ResponseResult asResp = articleClient.getArticleByTitle(apArticle);
            if (!asResp.getCode().equals(200)) {
                log.error("index={}文章检索失败", i);
                continue;
            }
            Object dbArticle = asResp.getData();
            if (dbArticle != null) {
                log.error("index={}文章重复，已跳过", i);
                continue;
            }

            // 获取id
            Integer id = article.getInteger("id");

            // 构造dto
            ArticleDto articleDto = new ArticleDto();
            articleDto.setTitle(article.getString("title"));
            articleDto.setAuthorId(info.getAuthorId());
            articleDto.setAuthorName(info.getAuthorName());
            articleDto.setChannelId(info.getChannelId());
            articleDto.setChannelName(info.getChannelName());
            articleDto.setLayout((short) 1);
            articleDto.setImages(article.getString("img_url"));
            articleDto.setLabels(info.getSearchWord());

            JSONObject content = article.getJSONObject("content");
            articleDto.setNation(content.getString("nation"));
            articleDto.setProvince(content.getString("province"));
            String publishTimeStr = content.getString("publish_time");
            if (StringUtils.isBlank(publishTimeStr))
                continue;
            Date publishTime = sdf.parse(publishTimeStr);
            articleDto.setCreatedTime(publishTime);
            articleDto.setPublishTime(publishTime);
            articleDto.setDescription(article.getString("description"));
            articleDto.setContent(content.getString("content"));

            // 上传图片
            Integer totalImg = article.getInteger("total_img");
            // 先上传封面
            String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
            String imgBasePath = basePath + File.separator + "article_img";
            String coverPath = imgBasePath + File.separator + id + ".jpg";
            BufferedInputStream coverIs = new BufferedInputStream(Files.newInputStream(Paths.get(coverPath)));
            String url = fileStorageService.uploadImgFile("", fileName, coverIs);
            coverIs.close();
            articleDto.setImages(url);

            // 上传正文图片
            String contStr = articleDto.getContent();
            if (StringUtils.isNotBlank(contStr)) {
                Queue<String> urls = new LinkedList<>();
                for (int j = 0; j < totalImg; j++) {
                    fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
                    String imgPath = imgBasePath + File.separator + id + "_"+ j + ".jpg";
                    BufferedInputStream contentPicIs = new BufferedInputStream(Files.newInputStream(Paths.get(imgPath)));
                    String content_url = fileStorageService.uploadImgFile("", fileName, contentPicIs);
                    contentPicIs.close();
                    urls.offer(content_url);
                }
                JSONArray conFragment = JSON.parseArray(contStr);
                for (int j = 0; j < conFragment.size(); j++) {
                    JSONObject fragment = conFragment.getJSONObject(j);
                    if ("image".equals(fragment.getString("type")))
                        fragment.put("value", urls.poll());
                }
                contStr = JSON.toJSONString(conFragment);
            } else {
                continue;
            }
            articleDto.setContent(contStr);

            try {
                ResponseResult res = articleClient.saveArticle(articleDto);
                if (!res.getCode().equals(200))
                    throw new RuntimeException();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("index={}文章插入失败", id);
            }

            log.info("index={}已完成", id);

//            break;
        }

        log.info(">>>>>>>>>> {} 任务已完成", info.getSearchWord());
    }
}
