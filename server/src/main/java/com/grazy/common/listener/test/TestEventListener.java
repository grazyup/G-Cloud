package com.grazy.common.listener.test;

import com.grazy.common.event.test.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-03-30 15:57
 * @Description: 测试线程池异步事件处理器
 */

@Component
@Slf4j
public class TestEventListener {

    /**
     * 监听测试事件
     *
     * @param event
     * @throws InterruptedException
     */
//    @TransactionalEventListener()
    @EventListener(TestEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void test(TestEvent event) throws InterruptedException {
        //当前线程池中调用线程休眠2秒，因为是异步此次休眠不会影响主线程的业务执行
        Thread.sleep(2000);
        log.info("TestEventListener start process, the thread name is {}", Thread.currentThread().getName());
    }
}
