package com.grazy.modules.user.context;

import com.grazy.modules.user.domain.GCloudUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-07 4:53
 * @Description: 在线修改密码 业务的上下文传参对象
 */

@Data
public class OnlineChangePasswordContext implements Serializable {

    private static final long serialVersionUID = -4750349733848382801L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 当前登录用户对象实体
     */
    private GCloudUser entity;
}
