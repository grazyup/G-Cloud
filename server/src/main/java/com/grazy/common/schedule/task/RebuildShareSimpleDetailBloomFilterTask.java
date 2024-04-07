package com.grazy.common.schedule.task;

import com.grazy.bloom.filter.core.BloomFilter;
import com.grazy.bloom.filter.core.BloomFilterManager;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.taskInterface.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: grazy
 * @Date: 2024-04-07 23:24
 * @Description: 定时重建简单分享详情布隆过滤器任务
 */

@Component
@Slf4j
public class RebuildShareSimpleDetailBloomFilterTask implements ScheduleTask {

    @Resource
    private GCloudShareService shareService;

    @Resource
    private BloomFilterManager manager;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    /**
     * 获取定时任务的名称
     *
     * @return
     */
    @Override
    public String getScheduleTaskName() {
        return "RebuildShareSimpleDetailBloomFilterTask";
    }


    /**
     * 执行重建任务
     */
    @Override
    public void run() {
        log.info("start rebuild ShareSimpleDetailBloomFilter...");
        BloomFilter<Long> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.isNull(bloomFilter)) {
            log.info("the bloomFilter named {} is null, give up rebuild...", BLOOM_FILTER_NAME);
            return;
        }
        bloomFilter.clear();

        long startId = 0L;
        long limit = 10000L;
        AtomicLong addCount = new AtomicLong(0L);

        List<Long> shareIdList;

        do {
            shareIdList = shareService.rollingQueryShareId(startId, limit);
            if (CollectionUtils.isNotEmpty(shareIdList)) {
                shareIdList.stream().forEach(shareId -> {
                    bloomFilter.put(shareId);
                    addCount.incrementAndGet();
                });
                startId = shareIdList.get(shareIdList.size() - 1);
            }
        } while (CollectionUtils.isNotEmpty(shareIdList));

        log.info("finish rebuild ShareSimpleDetailBloomFilter, total set item count {}...", addCount.get());
    }
}
