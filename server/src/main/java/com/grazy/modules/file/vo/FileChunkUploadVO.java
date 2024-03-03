package com.grazy.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-03 20:28
 * @Description: 文件分片上传服务器响应类
 */

@ApiModel(value = "文件分片上传的响应实体")
@Data
public class FileChunkUploadVO implements Serializable {

    private static final long serialVersionUID = -9106589573818029320L;

    @ApiModelProperty(value = "是否合并文件 0-不需要 1-需要")
    private Integer mergeFlag;
}
