package com.grazy.modules.user.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-07 3:48
 * @Description: 忘记密码-重置密码 前端传参交互对象类
 */

@Data
public class PasswordResetContext implements Serializable {

    private static final long serialVersionUID = 4951075934240506549L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 新密码
     */
    private String password;

    /**
     * 提交重置密码的临时身份牌凭证token
     */
    private String token;
}
