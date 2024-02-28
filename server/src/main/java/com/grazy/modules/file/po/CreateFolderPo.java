package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-19 8:07
 * @Description: 创建文件夹 前端传参交互对象类
 */

@ApiModel(value = "创建文件夹参数")
@Data
public class CreateFolderPo implements Serializable {

    private static final long serialVersionUID = -5558825951516309431L;

    @ApiModelProperty(value = "父文件夹的加密ID",required = true)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty(value = "文件夹名称",required = true)
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

}
