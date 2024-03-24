package com.grazy.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-24 20:49
 * @Description: 创建分享链接URL前端交互参数
 */

@ApiModel("创建分享链接URL前端交互参数")
@Data
public class CreateShareUrlPo implements Serializable {

    private static final long serialVersionUID = -441231970945706715L;

    @ApiModelProperty(value = "分享链接的名称",required = true)
    @NotBlank(message = "分享链接的名称不能为空")
    private String shareName;

    @ApiModelProperty(value = "分享的文件提取类型 0-有提取码",required = true)
    @NotNull(message = "分享的类型不能为空")
    private Integer shareType;

    @ApiModelProperty(value = "分享的文件有效天数类型 （0 永久有效；1 7天有效；2 30天有效）", required = true)
    @NotNull(message = "分享的日期类型不能为空")
    private Integer shareDayType;

    @ApiModelProperty(value = "分享的文件ID集合，多个使用公用的分割符去拼接", required = true)
    @NotBlank(message = "分享的文件ID不能为空")
    private String shareFileIds;
}
