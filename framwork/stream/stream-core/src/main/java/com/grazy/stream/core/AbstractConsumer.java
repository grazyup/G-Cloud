package com.grazy.stream.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-04-11 8:50
 * @Description: 消费者的公用抽象父类  --> 主要实现公用逻辑的抽离
 */

@Slf4j
public abstract class AbstractConsumer {

    /**
     * 公用的消息打印日志
     *
     * @param message
     */
    protected void printLog(Message message) {
        log.info("{} start consume message, the message is {}", this.getClass().getSimpleName(), message);
    }


    /**
     * 公用的空消息校验参数
     *
     * @param message
     * @return
     */
    protected boolean isEmptyMessage(Message<Object> message){
        if(Objects.isNull(message)){
            return true;
        }
        Object payload = message.getPayload();
        if(Objects.isNull(payload)){
            return true;
        }
        return false;
    }
}
