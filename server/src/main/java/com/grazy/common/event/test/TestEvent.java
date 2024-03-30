package com.grazy.common.event.test;

import org.springframework.context.ApplicationEvent;

/**
 * @Author: grazy
 * @Date: 2024-03-30 15:55
 * @Description: 测试使用线程池中的线程处理监听任务
 */

public class TestEvent extends ApplicationEvent {

    private String message;

    public TestEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

}
