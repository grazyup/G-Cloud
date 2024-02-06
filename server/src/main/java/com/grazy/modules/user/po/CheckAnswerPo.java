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
 * @Date: 2024-02-07 3:09
 * @Description: 忘记密码-检验密保答案 前端传参交互对象类
 */

@Data
@ApiModel("忘记密码-检验密保答案参数")
public class CheckAnswerPo implements Serializable {

    private static final long serialVersionUID = 8133628211270951982L;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空!")
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$",message = "请输入6-16位只包含数字和字母的用户名")
    private String username;


    @ApiModelProperty(value = "密保问题", required = true)
    @NotBlank(message = "密保问题不能为空")
    @Length(max = 100, message = "密保问题不能超过100字")
    private String question;

    @ApiModelProperty(value = "密保答案", required = true)
    @NotBlank(message = "密保答案不能为空")
    @Length(max = 100, message = "密保答案不能超过100字")
    private String answer;
}
