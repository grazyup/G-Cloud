package com.grazy.common.stream.consumer.log;

import com.grazy.common.stream.channel.GCloudChannels;
import com.grazy.common.stream.event.log.ErrorLogEvent;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.log.domain.GCloudErrorLog;
import com.grazy.modules.log.service.GCloudErrorLogService;
import com.grazy.stream.core.AbstractConsumer;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2024-04-13 14:20
 * @Description: 错误日志事件监听器
 */

@Component
public class ErrorLogEventListener extends AbstractConsumer {

    @Resource
    private GCloudErrorLogService gCloudErrorLogService;

    /**
     * 保存错误日志到数据库
     *
     * @param message
     */
    @StreamListener(GCloudChannels.ERROR_LOG_INPUT)
    public void saveErrorLogRecord(Message<ErrorLogEvent> message) {
        if (this.isEmptyMessage(message)) {
            return;
        }
        this.printLog(message);
        ErrorLogEvent event = message.getPayload();
        GCloudErrorLog gCloudErrorLog = new GCloudErrorLog();
        gCloudErrorLog.setLogStatus(0);
        gCloudErrorLog.setLogContent(event.getErrorMsg());
        gCloudErrorLog.setId(IdUtil.get());
        gCloudErrorLog.setCreateUser(event.getUserId());
        gCloudErrorLog.setUpdateUser(event.getUserId());
        gCloudErrorLog.setCreateTime(new Date());
        gCloudErrorLog.setUpdateTime(new Date());
        gCloudErrorLogService.save(gCloudErrorLog);
    }

}
