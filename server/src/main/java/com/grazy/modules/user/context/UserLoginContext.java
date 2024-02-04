package com.grazy.modules.user.context;

import com.grazy.modules.user.domain.GCloudUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-04 8:18
 * @Description: 用户注册业务的上下文传参对象
 */

@Data
public class UserLoginContext implements Serializable {

    private static final long serialVersionUID = 3450144964109148421L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录凭证
     */
    private String accessToken;

    /**
     * 用户对象实体
     */
    private GCloudUser entity;
}
