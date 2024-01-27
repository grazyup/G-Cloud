package com.grazy.taskInterface;

/**
 * @Author: grazy
 * @Date: 2024-01-27 18:52
 * @Description: 定时任务执行接口
 */

public interface ScheduleTask extends Runnable { //Runnable是一个线程接口

    /**
     * 获取定时任务名称
     */
    String getScheduleTaskName();

}
