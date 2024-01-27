package com.grazy;

import com.grazy.config.taskConfig;
import com.grazy.taskManager.ScheduleTaskManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-01-27 19:37
 * @Description: 测试启动类
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = taskConfig.class)
public class taskManagerTest {

    @Resource
    private ScheduleTaskManager scheduleTaskManager;

    @Resource
    private com.grazy.task.task task;

    @Test
    public void test() throws Exception{
        String cron = "0/5 * * * * ? ";
        //启动定时任务
        String key = scheduleTaskManager.startTask(task, cron);
        Thread.sleep(10000);
        //更新定时任务时间
        String newKey = scheduleTaskManager.updateTask(key, "0/1 * * * * ? ");
        Thread.sleep(10000);
        //停止定时任务
        scheduleTaskManager.stopTask(newKey);

    }
}
