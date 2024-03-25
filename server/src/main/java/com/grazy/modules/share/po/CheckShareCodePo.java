package com.grazy.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-25 22:37
 * @Description: 校验分享提取码前端交互参数
 */

@ApiModel("校验分享提取码前端交互参数")
@Data
public class CheckShareCodePo implements Serializable {

    private static final long serialVersionUID = 3487402925694593962L;

    @ApiModelProperty(value = "分享链接ID",required = true)
    @NotBlank(message = "分享链接ID不能为空")
    private String shareId;

    @ApiModelProperty(value = "分享链接提取码",required = true)
    @NotBlank(message = "分享链接提取码不能为空")
    private String shareCode;
}
