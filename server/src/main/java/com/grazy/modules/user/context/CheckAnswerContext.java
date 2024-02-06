package com.grazy.modules.user.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-07 3:09
 * @Description: 忘记密码-检验密保答案 业务的上下文传参对象
 */

@Data
public class CheckAnswerContext implements Serializable {

    private static final long serialVersionUID = 3927134982600101618L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;
}
