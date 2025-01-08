import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/1/1-03:04:14
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private TaskService taskService;

    @Test
    public void testList() {

//        cacheService.lLeftPush("list_001", "hello, redis");

        String list001 = cacheService.lRightPop("list_001");
        System.out.println(list001);

    }

    @Test
    public void testAddTask() {
//        Task task = new Task();
//        task.setTaskType(100);
//        task.setPriority(50);
//        task.setParameters("task test".getBytes());
//        task.setExecuteTime(new Date().getTime());
//
//        Long taskId = taskService.addTask(task);
//        System.out.println(taskId);
        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTaskType(100 + i);
            task.setPriority(50);
            task.setParameters("task test".getBytes());
            task.setExecuteTime(new Date().getTime() + 500 * i);

            taskService.addTask(task);
        }
    }

    @Test
    public void testRemoveTask() {
        taskService.cancelTask(1874440011589812226L);
    }

    @Test
    public void testPullTask() {
        taskService.pollTask(100, 50);
    }

}
