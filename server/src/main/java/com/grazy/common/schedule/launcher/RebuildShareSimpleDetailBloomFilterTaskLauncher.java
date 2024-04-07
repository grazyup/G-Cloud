package com.grazy.common.schedule.launcher;

import com.grazy.common.schedule.task.RebuildShareSimpleDetailBloomFilterTask;
import com.grazy.taskManager.ScheduleTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-04-07 23:31
 * @Description: 定时重建简单分享详情布隆过滤器任务触发器
 */

@Component
@Slf4j
public class RebuildShareSimpleDetailBloomFilterTaskLauncher implements CommandLineRunner {

    private final static String CRON = "1 0 0 * * ? ";

    @Resource
    private RebuildShareSimpleDetailBloomFilterTask task;

    @Resource
    private ScheduleTaskManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }

}
