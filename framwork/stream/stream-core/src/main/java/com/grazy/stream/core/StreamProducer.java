package com.grazy.stream.core;

import java.util.Map;

/**
 * @Author: grazy
 * @Date: 2024-04-10 22:21
 * @Description: 消息发送者顶级接口
 */

public interface StreamProducer {

    /**
     * 发送消息
     *
     * @param channelName
     * @param deploy
     * @return
     */
    boolean sendMessage(String channelName, Object deploy);


    /**
     * 发送消息
     *
     * @param channelName
     * @param deploy
     * @param headers
     * @return
     */
    boolean sendMessage(String channelName, Object deploy, Map<String,Object> headers);
}
