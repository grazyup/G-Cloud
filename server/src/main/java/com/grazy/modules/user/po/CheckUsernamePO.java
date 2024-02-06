package com.grazy.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-07 2:38
 * @Description: 忘记密码-用户名称校验 前端传参交互对象类
 */

@Data
@ApiModel("忘记密码-校验用户名参数")
public class CheckUsernamePO implements Serializable {

    private static final long serialVersionUID = -8862186598096134507L;


    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "用户名不能为空！")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字和字母的用户名")
    private String username;

}
