package com.grazy.modules.share.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-24 20:44
 * @Description:  创建分享链接响应实体参数
 */

@ApiModel("创建分享链接响应实体参数")
@Data
public class GCloudShareUrlVo implements Serializable {

    private static final long serialVersionUID = 9066925446645650027L;

    @ApiModelProperty("分享链接的名称")
    private String shareName;

    @ApiModelProperty("分享链接的ID")
    private Long shareId;

    @ApiModelProperty("分享链接的URL")
    private String shareUrl;

    @ApiModelProperty("分享链接的提取码")
    private String shareCode;

    @ApiModelProperty("分享链接的状态")
    private Integer shareStatus;
}
