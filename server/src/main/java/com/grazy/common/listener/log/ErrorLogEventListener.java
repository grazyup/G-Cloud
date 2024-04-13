//package com.grazy.common.listener.log;
//
//import com.grazy.common.event.log.ErrorLogEvent;
//import com.grazy.core.utils.IdUtil;
//import com.grazy.modules.log.domain.GCloudErrorLog;
//import com.grazy.modules.log.service.GCloudErrorLogService;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Date;
//
///**
// * @Author: grazy
// * @Date: 2024-03-02 22:39
// * @Description: 错误日志事件监听器
// */
//
//@Component
//public class ErrorLogEventListener {
//
//    @Resource
//    private GCloudErrorLogService gCloudErrorLogService;
//
//    /**
//     * 保存错误日志到数据库
//     * @param event
//     */
//    @Async(value = "eventListenerTaskExecutor")
//    @EventListener(ErrorLogEvent.class)
//    public void saveErrorLogRecord(ErrorLogEvent event){
//        GCloudErrorLog gCloudErrorLog = new GCloudErrorLog();
//        gCloudErrorLog.setLogStatus(0);
//        gCloudErrorLog.setLogContent(event.getErrorMsg());
//        gCloudErrorLog.setId(IdUtil.get());
//        gCloudErrorLog.setCreateUser(event.getUserId());
//        gCloudErrorLog.setUpdateUser(event.getUserId());
//        gCloudErrorLog.setCreateTime(new Date());
//        gCloudErrorLog.setUpdateTime(new Date());
//        gCloudErrorLogService.save(gCloudErrorLog);
//    }
//
//}
