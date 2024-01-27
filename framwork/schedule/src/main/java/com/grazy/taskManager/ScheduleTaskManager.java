package com.grazy.taskManager;

import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.UUIDUtil;
import com.grazy.holder.ScheduleTaskHolder;
import com.grazy.taskInterface.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2024-01-27 19:00
 * @Description: 定时任务管理器
 */

@Component
@Slf4j
public class ScheduleTaskManager {

    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private Map<String, ScheduleTaskHolder> scheduleTaskCache = new ConcurrentHashMap<>();


    /**
     * 启动定时任务
     *
     * @param scheduleTask 定时任务实现接口
     * @param cron         定时任务CRON表达式
     * @return 任务唯一标识
     */
    public String startTask(ScheduleTask scheduleTask, String cron) {
        //任务执行结果实体
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(scheduleTask, new CronTrigger(cron));
        String key = UUIDUtil.getUUID();
        //启动的任务保存到集合中
        scheduleTaskCache.put(key, new ScheduleTaskHolder(scheduledFuture, scheduleTask));
        log.info("{} 启动成功！唯一标识为:{}", scheduleTask.getScheduleTaskName(), key);
        return key;
    }


    /**
     * 停止定时任务
     *
     * @param key 定时任务唯一标识
     */
    public void stopTask(String key) {
        if (Strings.isBlank(key)) {
            return;
        }
        ScheduleTaskHolder task = scheduleTaskCache.get(key);
        if (Objects.isNull(task)) {
            return;
        }
        task.getScheduledFuture().cancel(true);
        log.info("{} 停止成功！唯一标识为:{}", task.getScheduleTask().getScheduleTaskName(), key);
    }


    /**
     * 更新定时任务时间
     *
     * @param key  旧任务的唯一标识
     * @param cron 定时任务cron表达式
     * @return 新任务的唯一标识
     */
    public String updateTask(String key, String cron) {
        if (StringUtils.isAnyBlank(key, cron)) {
            throw new GCloudBusinessException("定时任务的唯一标识和cron表达式不能为空！");
        }
        ScheduleTaskHolder task = scheduleTaskCache.get(key);
        if (Objects.isNull(task)) {
            throw new GCloudBusinessException("不存在此标识的任务！");
        }
        //获取旧任务实现接口
        ScheduleTask scheduleTask = task.getScheduleTask();
        //删除集合中旧任务
        scheduleTaskCache.remove(key);
        //启动新任务
        return this.startTask(scheduleTask, cron);
    }
}
