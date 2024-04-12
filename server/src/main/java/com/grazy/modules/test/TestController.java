package com.grazy.modules.test;

import com.grazy.common.annotation.LoginIgnore;
import com.grazy.common.event.test.TestEvent;
import com.grazy.common.stream.channel.GCloudChannels;
import com.grazy.core.response.R;
import com.grazy.stream.core.StreamProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-03-30 15:59
 * @Description: 测试监听处理器
 */

@RestController
public class TestController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Resource
    @Qualifier("defaultStreamProducer")
    private StreamProducer streamProducer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 测试事件发布
     *
     * @return
     */
    @GetMapping("test")
    @LoginIgnore
    public R test() {
        applicationContext.publishEvent(new TestEvent(this, "test"));
        return R.success();
    }


    /**
     * 测试stream接入rocketmq对象的事件发布
     *
     * @return
     */
    @GetMapping("stream/test")
    @LoginIgnore
    public R streamTest(String name) {
        com.grazy.common.stream.event.TestEvent testEvent = new com.grazy.common.stream.event.TestEvent();
        testEvent.setName(name);
        streamProducer.sendMessage(GCloudChannels.TEST_OUTPUT, testEvent);
        return R.success();
    }
}
