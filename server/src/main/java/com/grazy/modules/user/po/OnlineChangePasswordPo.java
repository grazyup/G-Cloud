package com.grazy.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-07 4:53
 * @Description: 在线修改密码 前端传参交互对象类
 */

@Data
@ApiModel("在线修改密码参数")
public class OnlineChangePasswordPo implements Serializable {

    private static final long serialVersionUID = 804514088296954455L;

    @ApiModelProperty("旧密码")
    @NotBlank(message = "旧密码不能为空")
    @Length(min = 8, max = 16, message = "请输入8-16位的旧密码")
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotBlank(message = "新密码不能为空")
    @Length(min = 8, max = 16, message = "请输入8-16位的旧密码")
    private String newPassword;
}
