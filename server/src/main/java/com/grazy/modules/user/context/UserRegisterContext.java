package com.grazy.modules.user.context;

import com.grazy.modules.user.domain.GCloudUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-01-29 23:28
 * @Description: 用户注册业务的上下文传参对象
 * (分类各个传参对象类是为了以后好维护)
 */

@Data
public class UserRegisterContext implements Serializable {

    private static final long serialVersionUID = 4803785508644596547L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;


    /**
     * 用户对象实体类
     */
    private GCloudUser entity;

}
