package com.grazy.task;

import com.grazy.taskInterface.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-01-27 19:39
 * @Description: 任务实体
 */
@Slf4j
@Component
public class task implements ScheduleTask {
    @Override
    public String getScheduleTaskName() {
        return "测试定时任务";
    }

    @Override
    public void run() {
        //这里面实现任务的业务需求
        log.info("开始测试定时任务...");
    }
}
