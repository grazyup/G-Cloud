package com.grazy.bloom.filter.test;

import com.grazy.bloom.filter.core.BloomFilter;
import com.grazy.bloom.filter.core.BloomFilterManager;
import com.grazy.core.constants.GCloudConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-04-07 20:16
 * @Description: 本地布隆过滤器测试
 */


@SpringBootTest(classes = LocalBloomFilterTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication(scanBasePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH + ".bloom.filter.local")
@Slf4j
public class LocalBloomFilterTest {

    @Resource
    private BloomFilterManager manager;


    /**
     * 测试本地布隆过滤器
     */
    @Test
    public void localBloomFilterTest(){
        BloomFilter<Integer> bloomFilter = manager.getFilter("test");
        long failNum = 0;
        //向过滤器中添加元素
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(i);
        }
        //查看布隆过滤器的容错率
        for (int j = 1000000; j < 1100000; j++) {
            boolean result = bloomFilter.mightContain(j);
            if(result){
                failNum++;
            }
        }
        log.info("test num {}, fail num {}", 100000, failNum);
    }

}
