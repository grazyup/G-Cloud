//package com.grazy.common.event.search;
//
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import org.springframework.context.ApplicationEvent;
//
///**
// * @Author: grazy
// * @Date: 2024-03-12 17:47
// * @Description: 用户搜索事件
// */
//
//@Setter
//@Getter
//@EqualsAndHashCode
//@ToString
//public class SearchEvent extends ApplicationEvent {
//
//    /**
//     * 搜索关键词
//     */
//    private String keyword;
//
//    /**
//     * 当前登录的用户ID
//     */
//    private Long userId;
//
//
//    public SearchEvent(Object source, String keyword, Long userId) {
//        super(source);
//        this.keyword = keyword;
//        this.userId = userId;
//    }
//}
