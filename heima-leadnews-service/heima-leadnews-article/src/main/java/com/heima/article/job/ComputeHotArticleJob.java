package com.heima.article.job;

import com.heima.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ComputeHotArticleJob {

    @Autowired
    private HotArticleService hotArticleService;

    @XxlJob("syncArticleInfo")
    public void syncInfo() {
        log.info(">>>>>>>>>>> 开始同步文章信息");
        hotArticleService.syncArticleInfo();
        log.info(">>>>>>>>>>> 同步文章信息完成");
    }

    @XxlJob("computeHotArticleJob")
    public void handle() {
        log.info(">>>>>>>>>>> 热文章分值计算调度任务执行");
        hotArticleService.computeHotArticle();
        log.info(">>>>>>>>>>> 热文章分值计算调度任务执行完成");
    }
}
