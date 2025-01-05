package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/1/1-03:28:43
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 添加延迟任务
     * @param task
     * @return
     */
    @Override
    @Transactional
    public Long addTask(Task task) {
        // 1.添加任务到数据库中
        // 保存任务表
        Taskinfo taskinfo = new Taskinfo();
        BeanUtils.copyProperties(task, taskinfo);
        taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
        taskinfoMapper.insert(taskinfo);
        // 保存任务日志数据
        TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
        BeanUtils.copyProperties(taskinfo, taskinfoLogs);
        taskinfoLogs.setVersion(0);
        taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
        taskinfoLogsMapper.insert(taskinfoLogs);

        // 2.添加任务到redis
        task.setTaskId(taskinfo.getTaskId());
        addTaskToCache(task);

        return taskinfo.getTaskId();
    }

    @Override
    @Transactional
    public void cancelTask(Long taskId) {
        // 删除任务，更新任务日志
        Task task = updateDB(taskId, ScheduleConstants.CANCELLED);
        // 删除redis的数据
        String key = task.getTaskType() + "_" + task.getPriority();
        cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
    }

    @Override
    @Transactional
    public Task pollTask(int type, int priority) {
        String key = type + "_" + priority;
        // 从redis中拉取数据
        String taskJson = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
        Task task = null;
        if (StringUtils.isNotBlank(taskJson)) {
            task = JSON.parseObject(taskJson, Task.class);
            // 修改数据库信息
            updateDB(task.getTaskId(), ScheduleConstants.EXECUTED);
        }

        return task;
    }

    /**
     * 未来数据定时刷新
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh() {
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);  // 获取悲观锁
        if (StringUtils.isNotBlank(token)) {  // 查看是否获取到悲观锁，若获取到则刷新
            log.info("未来数据定时刷新---定时任务");
            // 获取所有未来数据集合key
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {
                // 获取当前数据的topic key
                String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];

                // 按照key和score查询符合条件的数据
                Set<String> toBeExecuted = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                // 同步数据
                if (!toBeExecuted.isEmpty()) {
                    cacheService.refreshWithPipeline(futureKey, topicKey, toBeExecuted);
                    log.info("成功将{}刷新到{}", futureKey, topicKey);
                }
            }
        }
    }

    @Transactional
    @PostConstruct
    @Scheduled(cron = "0 */5 * * * *")
    public void reloadCache() {
        // 删除缓存中未来数据集合和当前消费者队列的所有key
        Set<String> fKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        Set<String> tKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        cacheService.delete(fKeys);
        cacheService.delete(tKeys);

        // 查找五分钟以内的任务
        Calendar time = Calendar.getInstance();
        time.add(Calendar.MINUTE, 5);
        List<Taskinfo> tasks = taskinfoMapper.selectList(new LambdaQueryWrapper<Taskinfo>()
                .le(Taskinfo::getExecuteTime, time.getTime()));
        if (tasks != null) {
            for (Taskinfo taskinfo : tasks) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskToCache(task);
            }
            log.info("成功刷新{}条缓存", tasks.size());
        }
    }

    private void addTaskToCache(Task task) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        String key = task.getTaskType() + "_" + task.getPriority();
        long nextScheduleTime = calendar.getTimeInMillis();
        // 2.1 任务执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis())
            cacheService.lLeftPush(
                    ScheduleConstants.TOPIC + key,
                    JSON.toJSONString(task));
            // 2.2 任务执行时间大于当前时间 && 小于未来5分钟，存入zset中
        else if (task.getExecuteTime() <= nextScheduleTime)
            cacheService.zAdd(ScheduleConstants.FUTURE + key,
                    JSON.toJSONString(task),
                    task.getExecuteTime());
    }

    private Task updateDB(Long taskId, Integer status) {
        taskinfoMapper.deleteById(taskId);
        TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
        taskinfoLogs.setStatus(status);
        taskinfoLogsMapper.updateById(taskinfoLogs);

        Task task = new Task();
        BeanUtils.copyProperties(taskinfoLogs, task);
        task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        return task;
    }
}
