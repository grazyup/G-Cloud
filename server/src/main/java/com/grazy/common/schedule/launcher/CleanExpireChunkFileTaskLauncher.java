package com.grazy.common.schedule.launcher;

import com.grazy.common.schedule.task.CleanExpireChunkFileTask;
import com.grazy.taskManager.ScheduleTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-03-20 12:48
 * @Description: 定时清理过期的文件分片任务触发器
 */

@Component
@Slf4j
public class CleanExpireChunkFileTaskLauncher implements CommandLineRunner {

    @Resource
    private ScheduleTaskManager scheduleTaskManager;

    @Resource
    private CleanExpireChunkFileTask task;

    private final static String CRON = "1 0 0 * * ? ";

    @Override
    public void run(String... args) throws Exception {
        scheduleTaskManager.startTask(task,CRON);
    }
}
