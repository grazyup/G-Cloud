package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-06 22:44
 * @Description: 合并分片文件 前端交互参数
 */

@ApiModel(value = "分片文件合并参数")
@Data
public class FileChunkMergePo implements Serializable {

    private static final long serialVersionUID = 6466048121996181387L;

    @ApiModelProperty(value = "文件名称",required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件的唯一标识",required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

    @ApiModelProperty(value = "父文件夹ID",required = true)
    @NotNull(message = "父文件夹ID不能为空")
    private Long parentId;

    @ApiModelProperty(value = "文件总大小",required = true)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;
}
