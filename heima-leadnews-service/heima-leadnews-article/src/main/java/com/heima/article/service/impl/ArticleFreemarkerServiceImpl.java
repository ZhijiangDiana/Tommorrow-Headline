package com.heima.article.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/30-04:16:39
 */
@Service
@Slf4j
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    /**
     * 生成静态文件并上传到minio中
     * @param apArticle
     * @param content
     */
//    @Async  // XA模式下调用链上的业务不能是异步
    @Override
    @Transactional
    public void buildArticleToMinio(ApArticle apArticle, String content) {
        Template template = null;
        try {
//            int a = 1/0;
            template = configuration.getTemplate("article.ftl");
            StringWriter out = new StringWriter();
            // 准备数据模型
            HashMap<String, Object> contentDataModel = new HashMap<>();
            content = content.replace("\\n", "<br/>");
            contentDataModel.put("content", JSONArray.parseArray(content));
            // 合成
            template.process(contentDataModel, out);
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);
            apArticleService.update(
                    Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId, apArticle.getId())
                            .set(ApArticle::getStaticUrl, path)
                            .set(ApArticle::getUpdatedTime, new Date()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
