package com.grazy.common.stream.event.log;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-02 22:33
 * @Description: 错误日志事件
 */

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ErrorLogEvent implements Serializable {

    private static final long serialVersionUID = -498664464440291721L;

    /**
     * 错误日志内容
     */
    private String errorMsg;

    /**
     * 当前登录用户ID
     */
    private Long userId;

    public ErrorLogEvent(String errorMsg, Long userId) {
        this.errorMsg = errorMsg;
        this.userId = userId;
    }
}
