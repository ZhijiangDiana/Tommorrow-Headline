package com.heima.schedule.feign;

import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleClient implements IScheduleClient {

    @Autowired
    private TaskService taskService;

    /**
     * 添加延迟任务
     * @return
     */
    @PostMapping("/api/v1/task/add")
    public ResponseResult addTask(@RequestBody Task task) {
        return ResponseResult.okResult(taskService.addTask(task));
    }

    /**
     * 取消任务
     *
     * @param taskId
     */
    @PostMapping("/api/v1/task/{taskId}")
    public ResponseResult cancelTask(@PathVariable Long taskId) {
        taskService.cancelTask(taskId);
        return ResponseResult.okResult(true);
    }

    /**
     * 拉取任务
     * @param type
     * @param priority
     * @return
     */
    @PostMapping("/api/v1/task/{type}/{priority}")
    public ResponseResult pollTask(@PathVariable int type, @PathVariable int priority) {
        return ResponseResult.okResult(taskService.pollTask(type, priority));
    }
}
