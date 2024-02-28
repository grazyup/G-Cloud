package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-28 21:55
 * @Description: 文件重命名 前端传参交互对象类
 */

@ApiModel(value = "文件重命名参数")
@Data
public class UpdateFilenamePo implements Serializable {

    private static final long serialVersionUID = -6135483597972735196L;

    @ApiModelProperty(value = "更新的文件ID", required = true)
    @NotBlank(message = "更新的文件ID不能为空")
    private String fileId;

    @ApiModelProperty(value = "新的文件名称", required = true)
    @NotBlank(message = "新的文件名称不能为空")
    private String newFilename;

}
