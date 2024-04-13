//package com.grazy.common.event.log;
//
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import org.springframework.context.ApplicationEvent;
//
///**
// * @Author: grazy
// * @Date: 2024-03-02 22:33
// * @Description: 错误日志事件
// */
//
//@Setter
//@Getter
//@EqualsAndHashCode
//@ToString
//public class ErrorLogEvent extends ApplicationEvent {
//
//    /**
//     * 错误日志内容
//     */
//    private String errorMsg;
//
//    /**
//     * 当前登录用户ID
//     */
//    private Long userId;
//
//    public ErrorLogEvent(Object source, String errorMsg, Long userId) {
//        super(source);
//        this.errorMsg = errorMsg;
//        this.userId = userId;
//    }
//}
