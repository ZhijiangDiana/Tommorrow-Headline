package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/1/1-03:27:58
 */
public interface TaskService {

    /**
     * 添加延迟任务
     * @return
     */
    Long addTask(Task task);

    /**
     * 取消任务
     *
     * @param taskId
     */
    void cancelTask(Long taskId);

    /**
     * 拉取任务
     * @param type
     * @param priority
     * @return
     */
    Task pollTask(int type, int priority);
}
