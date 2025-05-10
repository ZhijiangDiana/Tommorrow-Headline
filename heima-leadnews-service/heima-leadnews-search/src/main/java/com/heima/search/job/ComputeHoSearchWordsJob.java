package com.heima.search.job;

import com.heima.search.service.ApHotSearchWordsService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ComputeHoSearchWordsJob {

    @Autowired
    private ApHotSearchWordsService apHotSearchWordsService;

    @XxlJob("computeHotSearchWordsJob")
    public void handle() {
        log.info(">>>>>>>>>>> 热搜索词计算调度任务执行");
        apHotSearchWordsService.calculateHotSearchWords();
        log.info(">>>>>>>>>>> 热搜索词计算调度任务执行完成");
    }
}
