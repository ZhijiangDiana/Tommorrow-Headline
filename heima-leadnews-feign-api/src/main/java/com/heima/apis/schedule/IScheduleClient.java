package com.heima.apis.schedule;

import com.heima.apis.schedule.fallback.IScheduleFallback;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-schedule", fallback = IScheduleFallback.class)
public interface IScheduleClient {

    /**
     * 添加延迟任务
     * @return
     */
    @PostMapping("/api/v1/task/add")
    ResponseResult addTask(@RequestBody Task task);

    /**
     * 取消任务
     *
     * @param taskId
     */
    @PostMapping("/api/v1/task/{taskId}")
    ResponseResult cancelTask(@PathVariable Long taskId);

    /**
     * 拉取任务
     * @param type
     * @param priority
     * @return
     */
    @PostMapping("/api/v1/task/{type}/{priority}")
    ResponseResult pollTask(@PathVariable int type, @PathVariable int priority);
}
