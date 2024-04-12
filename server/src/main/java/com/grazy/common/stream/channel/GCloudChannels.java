package com.grazy.common.stream.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

/**
 * @Author: grazy
 * @Date: 2024-04-12 15:04
 * @Description: 声明事件传递通道
 *                  --> 配置文件加载完成后，channel通道就已经创建了，此处只是声明了通道的存在和它们的名称
 *                      （名称可以不用设置--注解的value值，但是不设置名称的情况下，方法名必须与配置文件中的配置的channel名称一致,这样才能匹配到;
 *                          若设置了名称，就根据设置的名称来匹配,方法名可以不用一致）
 */

public interface GCloudChannels {

    String TEST_INPUT = "testInput";

    String TEST_OUTPUT = "testOutput";


    /**
     * 测试输入通道
     *
     * @return
     */
    @Input(TEST_INPUT)
    SubscribableChannel testInput();


    /**
     * 测试输出通道
     *
     * @return
     */
    @Output(TEST_OUTPUT)
    SubscribableChannel testOutput();
}
