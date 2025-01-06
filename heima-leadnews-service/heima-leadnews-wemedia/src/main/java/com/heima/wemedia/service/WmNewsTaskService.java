package com.heima.wemedia.service;

import java.util.Date;

public interface WmNewsTaskService {

    /**
     * 添加审核任务到延迟队列中
     * @param id
     * @param scanTime
     */
    void addScanNewsTask(Integer id, Date scanTime);

    /**
     * 添加发布任务到延迟队列中
     * @param id
     * @param publishTime
     */
    void addPublishNewsTask(Integer id, Date publishTime);

    /**
     * 消费审核任务
     */
    void getScanNewsTask();

    /**
     * 消费发布任务
     */
    void getPublishNewsTask();
}
