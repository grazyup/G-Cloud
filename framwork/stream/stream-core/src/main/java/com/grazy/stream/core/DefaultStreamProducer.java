package com.grazy.stream.core;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-04-11 8:47
 * @Description: 默认的消息发送实体 （不使用发送消息的消息实体前后操作）
 */

@Component(value = "defaultStreamProducer")
public class DefaultStreamProducer extends AbstractStreamProducer{

    /**
     * 发送消息的后置钩子函数
     *
     * @param message
     * @param result
     */
    @Override
    protected void afterSend(Message<Object> message, boolean result) {

    }


    /**
     * 发送消息的前置钩子函数
     *
     * @param message
     */
    @Override
    protected void preSend(Message<Object> message) {

    }
}
