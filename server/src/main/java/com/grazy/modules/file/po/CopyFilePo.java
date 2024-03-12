package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-12 14:34
 * @Description: 文件复制 前端交互参数
 */

@ApiModel(value = "文件复制参数")
@Data
public class CopyFilePo implements Serializable {

    private static final long serialVersionUID = -3322149427754272786L;

    @ApiModelProperty(value = "要复制的文件ID集合，多个文件使用公用的分隔符分隔")
    @NotBlank(message = "请选择要移动的文件")
    private String fileIds;

    @ApiModelProperty(value = "要复制到的目标文件夹的ID")
    @NotBlank(message = "请选择要复制到哪个文件夹下面")
    private String targetParentId;
}
