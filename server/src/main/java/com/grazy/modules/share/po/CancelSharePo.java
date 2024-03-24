package com.grazy.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-25 0:53
 * @Description: 取消分享链接前端交互参数
 */

@ApiModel("取消分享链接前端交互参数")
@Data
public class CancelSharePo implements Serializable {

    private static final long serialVersionUID = -4299708116946422592L;

    @ApiModelProperty(value = "要取消的分享ID的集合，多个使用公用的分割符拼接", required = true)
    @NotBlank(message = "请选择要取消的分享")
    private String shareIds;
}
