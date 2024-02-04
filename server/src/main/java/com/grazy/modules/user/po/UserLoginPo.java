package com.grazy.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-04 8:15
 * @Description: 用户登录前端传参交互对象类
 */

@Data
@ApiModel(value = "用户登录参数")
public class UserLoginPo implements Serializable {

    private static final long serialVersionUID = 1787991516422389777L;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空!")
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$",message = "请输入6-16位只包含数字和字母的用户名")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空!")
    @Length(min = 8 ,max = 16, message = "请输入8-16为的密码")
    private String password;

}
