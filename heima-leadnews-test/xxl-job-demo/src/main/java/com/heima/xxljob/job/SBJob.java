package com.heima.xxljob.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SBJob {

    int i = 1;

    @XxlJob("demoJobHandler")
    public void sbJob() {
        log.info("简单任务执行了{}次", i);
        i++;
    }
}
