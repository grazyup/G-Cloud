package com.grazy.common.stream.consumer;

import com.grazy.common.stream.channel.GCloudChannels;
import com.grazy.common.stream.event.TestEvent;
import com.grazy.stream.core.AbstractConsumer;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-04-12 15:10
 * @Description:
 */

@Component
public class TestConsumer extends AbstractConsumer {

    /**
     * 测试消费者消费消息
     *
     * @param message
     */
    @StreamListener(GCloudChannels.TEST_INPUT)
    public void consumerTestMessage(Message<TestEvent> message){
        this.printLog(message);
    }
}
