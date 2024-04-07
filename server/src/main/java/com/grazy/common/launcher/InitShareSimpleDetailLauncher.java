package com.grazy.common.launcher;

import cn.hutool.core.collection.CollectionUtil;
import com.grazy.bloom.filter.core.BloomFilter;
import com.grazy.bloom.filter.core.BloomFilterManager;
import com.grazy.modules.share.service.GCloudShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: grazy
 * @Date: 2024-04-07 22:51
 * @Description: 简单分享详情布隆过滤器初始化器 （将数据提前加载到过滤器中）
 */

@Component
@Slf4j
public class InitShareSimpleDetailLauncher implements CommandLineRunner {

    @Resource
    private GCloudShareService shareService;

    @Resource
    private BloomFilterManager manager;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";


    /**
     * 初始化过滤器中的数据，将分享id全部存到过滤器中
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("start init ShareSimpleDetailBloomFilter...");
        BloomFilter<Long> filter = manager.getFilter(BLOOM_FILTER_NAME);
        if(Objects.isNull(filter)){
            log.info("the bloomFilter name {} is null, give up init...", BLOOM_FILTER_NAME);
            return;
        }
        //清空之前的过滤器内容
        filter.clear();

        long startId = 0L;
        long limit = 10000L;
        AtomicLong addCount = new AtomicLong(0L);

        List<Long> shareIdList;

        do {
            shareIdList = shareService.rollingQueryShareId(startId, limit);
            if(CollectionUtil.isNotEmpty(shareIdList)){
                shareIdList.stream().forEach(shareId -> {
                    filter.put(shareId);
                    addCount.incrementAndGet();
                });
                startId = shareIdList.get(shareIdList.size() - 1);
            }
        }while (CollectionUtil.isNotEmpty(shareIdList));
        log.info("finish init ShareSimpleDetailBloomFilter, total set item count {}...", addCount.get());
    }
}
