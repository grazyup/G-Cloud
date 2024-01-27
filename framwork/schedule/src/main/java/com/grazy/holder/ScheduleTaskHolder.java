package com.grazy.holder;

import com.grazy.taskInterface.ScheduleTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author: grazy
 * @Date: 2024-01-27 18:56
 * @Description: 定时任务实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTaskHolder implements Serializable {

    /**
     * 执行任务的结果实体
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * 定时任务执行器
     */
    private ScheduleTask scheduleTask;
}
