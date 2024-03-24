package com.grazy.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.web.serializable.Date2StringSerializer;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-25 0:07
 * @Description: 查询分享链接列表响应实体参数
 */

@ApiModel("查询分享链接列表响应实体参数")
@Data
public class GCloudShareUrlListVo implements Serializable {

    private static final long serialVersionUID = -4926767713459536749L;

    @ApiModelProperty("分享文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享链接的名称")
    private String shareName;

    @ApiModelProperty("分享链接的URL")
    private String shareUrl;

    @ApiModelProperty("分享链接的提取码")
    private String shareCode;

    @ApiModelProperty("分享链接的状态")
    private Integer shareStatus;

    @ApiModelProperty("分享链接的提取方式类型 0-有提取码")
    private Integer shareType;

    @ApiModelProperty("分享链接的有效天数类型 （0 永久有效；1 7天有效；2 30天有效）")
    private Integer shareDayType;

    @ApiModelProperty("分享链接的失效时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Data shareEndTime;

    @ApiModelProperty("分享链接的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Data createTime;
}
