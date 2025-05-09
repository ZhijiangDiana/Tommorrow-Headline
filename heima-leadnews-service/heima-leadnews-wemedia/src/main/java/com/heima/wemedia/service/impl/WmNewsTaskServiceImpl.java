package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.JdkSerializeUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient;

    @Lazy
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Autowired
    private WmNewsMapper wmNewsMapper;

    /**
     * 添加审核任务到延迟队列中
     * @param id
     * @param scanTime
     */
    @Override
    @Transactional
    public void addScanNewsTask(Integer id, Date scanTime) {
        Task task = new Task();
        task.setExecuteTime(scanTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN.getPriority());
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(JdkSerializeUtil.serialize(wmNews));

        ResponseResult responseResult = scheduleClient.addTask(task);
        if (!responseResult.getCode().equals(200))
            throw new RuntimeException(getClass().getSimpleName() + "-文章添加审核任务失败");

        log.info("添加{}审核文章任务到延迟服务中", id);
    }

    /**
     * 添加发布任务到延迟队列中
     * @param id
     * @param publishTime
     */
    @Override
    public void addPublishNewsTask(Integer id, Date publishTime) {
        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_PUBLISH.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_PUBLISH.getPriority());
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
        task.setParameters(JdkSerializeUtil.serialize(wmNews));

        ResponseResult responseResult = scheduleClient.addTask(task);
        if (!responseResult.getCode().equals(200))
            throw new RuntimeException(getClass().getSimpleName() + "-文章添加发布任务失败");

        log.info("添加{}发布文章任务到延迟服务中", id);
    }

    /**
     * 消费审核任务，1秒1次
     */
    @Override
    @Scheduled(fixedDelay = 1000)
    public void getScanNewsTask() {
        ResponseResult res = scheduleClient.pollTask(
                TaskTypeEnum.NEWS_SCAN.getTaskType(),
                TaskTypeEnum.NEWS_SCAN.getPriority());
        if (res.getCode().equals(HttpStatus.OK.value()) && res.getData() != null) {
            Task task = JSON.parseObject(JSON.toJSONString(res.getData()), Task.class);
            WmNews wmNews = JdkSerializeUtil.deserialize(task.getParameters(), WmNews.class);
            wmNewsAutoScanService.autoScanWmNews(wmNews.getId());

            log.info("成功审核{}文章", wmNews.getId());
        }
    }

    /**
     * 消费发布任务，1秒1次
     */
    @Override
    @Scheduled(fixedDelay = 1000)
    public void getPublishNewsTask() {
        ResponseResult res = scheduleClient.pollTask(
                TaskTypeEnum.NEWS_PUBLISH.getTaskType(),
                TaskTypeEnum.NEWS_PUBLISH.getPriority());
        if (res.getCode().equals(HttpStatus.OK.value()) && res.getData() != null) {
            Task task = JSON.parseObject(JSON.toJSONString(res.getData()), Task.class);
            WmNews wmNews = JdkSerializeUtil.deserialize(task.getParameters(), WmNews.class);
            wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
            wmNewsMapper.updateById(wmNews);

            log.info("成功发布{}文章", wmNews.getId());
        }
    }
}
