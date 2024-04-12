package com.grazy.stream.core;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.grazy.core.exception.GCloudBusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-04-10 22:24
 * @Description: 消息发送者顶级抽象父类
 */

public abstract class AbstractStreamProducer implements StreamProducer {

    /**
     * 容器中获取消息通道集合 key为channelName
     */
    @Resource
    private Map<String, MessageChannel> channelMap;


    /**
     * 发送消息
     *
     * @param channelName
     * @param deploy
     * @return
     */
    @Override
    public boolean sendMessage(String channelName, Object deploy) {
        return this.sendMessage(channelName, deploy, Maps.newHashMap());
    }


    /**
     * 发送消息
     * 1.参数校验
     * 2.执行发送前的钩子函数
     * 3.执行发送操作
     * 4.执行发送后的钩子函数
     * 5.返回结果
     *
     * @param channelName
     * @param deploy
     * @param headers
     * @return
     */
    @Override
    public boolean sendMessage(String channelName, Object deploy, Map<String, Object> headers) {
        if (StringUtils.isBlank(channelName) || Objects.isNull(deploy)) {
            throw new GCloudBusinessException("the channelName or deploy can not be empty!");
        }
        if (CollectionUtil.isEmpty(channelMap)) {
            throw new GCloudBusinessException("the channelMap can not be empty!");
        }
        MessageChannel messageChannel = channelMap.get(channelName);
        if (Objects.isNull(messageChannel)) {
            throw new GCloudBusinessException("the channel named " + channelName + "can not found!");
        }
        Message<Object> message = MessageBuilder.createMessage(deploy, new MessageHeaders(headers));
        preSend(message);
        boolean result = messageChannel.send(message);
        afterSend(message, result);
        return result;
    }


    /**
     * 发送消息的后置钩子函数 （下沉到子类实现）
     *
     * @param message
     * @param result
     */
    protected abstract void afterSend(Message<Object> message, boolean result);


    /**
     * 发送消息的前置钩子函数 （下沉到子类实现）
     *
     * @param message
     */
    protected abstract void preSend(Message<Object> message);
}
