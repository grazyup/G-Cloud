package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-12 10:06
 * @Description: 移动文件 前端交互参数
 */

@ApiModel(value = "移动文件参数")
@Data
public class TransferFilePo implements Serializable {

    private static final long serialVersionUID = -6531490082795113275L;

    @ApiModelProperty(value = "要转移的文件ID集合，多个文件使用公用的分隔符分隔")
    @NotBlank(message = "请选择要移动的文件")
    private String fileIds;

    @ApiModelProperty(value = "要转移到的目标文件夹的ID")
    @NotBlank(message = "请选择要转移到哪个文件夹下面")
    private String targetParentId;
}
