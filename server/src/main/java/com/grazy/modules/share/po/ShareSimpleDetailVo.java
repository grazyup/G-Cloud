package com.grazy.modules.share.po;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.modules.share.vo.ShareUserInfoVo;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-27 12:13
 * @Description: 查询简单的分享链接详情
 */

@ApiModel("查询简单的分享链接详情")
@Data
public class ShareSimpleDetailVo implements Serializable {

    private static final long serialVersionUID = -6505701190110750446L;

    @ApiModelProperty("分享链接名称")
    private String shareName;

    @ApiModelProperty("分享链接ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享人信息")
    private ShareUserInfoVo shareUserInfoVO;
}
