package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-29 20:24
 * @Description: 文件秒传 前端交互参数对象
 */

@ApiModel(value = "文件秒传参数")
@Data
public class SecUploadFilePo implements Serializable {

    private static final long serialVersionUID = 3655313063881294216L;

    @ApiModelProperty(value = "文件的唯一标识", required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

    @ApiModelProperty(value = "秒传的父文件夹ID",required = true)
    @NotBlank(message = "秒传的父文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;
}
