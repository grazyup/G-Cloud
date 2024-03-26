package com.grazy.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-26 11:31
 * @Description: 分享创建者的个人信息响应参数
 */

@ApiModel("分享创建者的个人信息响应参数")
@Data
public class ShareUserInfoVo implements Serializable {

    private static final long serialVersionUID = 806157482051514526L;

    @ApiModelProperty("分享者的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    @ApiModelProperty("分享者的名称")
    private String username;
}
