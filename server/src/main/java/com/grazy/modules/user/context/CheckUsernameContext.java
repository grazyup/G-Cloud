package com.grazy.modules.user.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-07 2:38
 * @Description: 忘记密码-用户名称校验业务的上下文传参对象
 */

@Data
public class CheckUsernameContext implements Serializable {

    private static final long serialVersionUID = 1913141360618723189L;

    /**
     * 用户名
     */
    private String username;

}
