package com.grazy.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-28 9:43
 * @Description: 保存文件到我的网盘前端交互参数
 */

@ApiModel("保存文件到我的网盘前端交互参数")
@Data
public class ShareSavePo implements Serializable {

    private static final long serialVersionUID = -403003626384696532L;

    @ApiModelProperty(value = "需要转存的文件ID集合，多个使用公用分隔符进行分割",required = true)
    @NotBlank(message = "请选择要转存的文件ID")
    private String fileIds;

    @ApiModelProperty(value = "要转存的目标文件夹",required = true)
    @NotBlank(message = "要转存的目标文件夹不能为空")
    private String targetParentId;
}
